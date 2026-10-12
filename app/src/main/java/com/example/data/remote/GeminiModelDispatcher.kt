package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GeminiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.delay
import kotlin.math.pow

/**
 * Resilient dispatcher that manages Gemini model execution.
 * If quota is exceeded (HTTP 429, RESOURCE_EXHAUSTED, or quota error codes),
 * it automatically falls back sequentially across valid alternative models
 * (e.g., gemini-3.5-flash -> gemini-3.1-flash-lite-preview -> gemini-flash-latest -> gemini-3.1-pro-preview)
 * and alerts observing UI components of the active model change.
 */
object GeminiModelDispatcher {

    private const val TAG = "GeminiModelDispatcher"

    // Text & reasoning fallback cascade priority list of verified available models
    val fallbackChain = listOf(
        GeminiModel.GEMINI_3_5_FLASH,
        GeminiModel.GEMINI_FLASH_LATEST,
        GeminiModel.GEMINI_3_1_FLASH_LITE,
        GeminiModel.GEMINI_2_5_FLASH,
        GeminiModel.GEMINI_3_1_PRO
    )

    private val _activeModel = MutableStateFlow(GeminiModel.defaultModel())
    val activeModel: StateFlow<GeminiModel> = _activeModel.asStateFlow()

    private val _lastQuotaExceededEvent = MutableStateFlow<QuotaSwitchEvent?>(null)
    val lastQuotaExceededEvent: StateFlow<QuotaSwitchEvent?> = _lastQuotaExceededEvent.asStateFlow()

    data class QuotaSwitchEvent(
        val originalModel: GeminiModel,
        val switchedModel: GeminiModel,
        val timestamp: Long = System.currentTimeMillis(),
        val message: String
    )

    fun setActiveModel(model: GeminiModel) {
        _activeModel.value = model
    }

    /**
     * Checks whether an exception indicates that the quota was exceeded or rate limited.
     */
    fun isQuotaExceededException(e: Throwable): Boolean {
        if (e is HttpException) {
            val code = e.code()
            if (code == 429 || code == 503 || code == 403) return true
            val errorBody = try { e.response()?.errorBody()?.string().orEmpty().lowercase() } catch (ex: Exception) { "" }
            if (errorBody.contains("quota") || 
                errorBody.contains("resource_exhausted") || 
                errorBody.contains("rate limit") || 
                errorBody.contains("too_many_requests") ||
                errorBody.contains("overloaded")
            ) {
                return true
            }
        }
        val msg = e.message?.lowercase().orEmpty()
        return msg.contains("429") ||
                msg.contains("quota") ||
                msg.contains("resource_exhausted") ||
                msg.contains("rate limit") ||
                msg.contains("rate_limit_exceeded") ||
                msg.contains("too many requests")
    }

    /**
     * Checks whether an exception is a model-level failure (404 Not Found, 400 Bad Request / Unsupported modality, etc.)
     * that should cascade to the next candidate model instead of failing the user query.
     */
    fun isModelUnavailableException(e: Throwable): Boolean {
        if (e is HttpException) {
            val code = e.code()
            if (code == 404 || code == 400 || code == 500 || code == 502) return true
        }
        val msg = e.message?.lowercase().orEmpty()
        return msg.contains("404") || msg.contains("not found")
    }

    /**
     * Executes the provided Gemini API request. If a quota/rate limit or model-specific error occurs,
     * it automatically tries the next models in the fallback sequence.
     */
    suspend fun <T> executeWithQuotaFallback(
        preferredModel: GeminiModel? = null,
        call: suspend (model: GeminiModel) -> T
    ): QuotaExecutionResult<T> {
        val initial = preferredModel ?: _activeModel.value

        // Order: preferredModel first, then the rest of the fallback cascade without duplicates
        val candidates = mutableListOf<GeminiModel>()
        candidates.add(initial)
        for (m in fallbackChain) {
            if (m != initial) {
                candidates.add(m)
            }
        }

        var lastException: Throwable? = null

        for (i in candidates.indices) {
            val candidate = candidates[i]
            var retries = 0
            val maxRetries = 2
            
            while (retries <= maxRetries) {
                try {
                    Log.d(TAG, "Invoking Gemini with model: ${candidate.modelId} (Attempt ${retries + 1})")
                    val result = call(candidate)

                    // If we successfully executed on a model different from initial due to fallback,
                    // update active model and post event
                    if (candidate != initial) {
                        val event = QuotaSwitchEvent(
                            originalModel = initial,
                            switchedModel = candidate,
                            message = "Switched to ${candidate.displayName} to complete query successfully."
                        )
                        _lastQuotaExceededEvent.value = event
                        _activeModel.value = candidate
                        Log.w(TAG, "Auto-switched model from ${initial.displayName} to ${candidate.displayName}.")
                    }

                    return QuotaExecutionResult.Success(
                        data = result,
                        modelUsed = candidate,
                        wasSwitchedDueToQuota = (candidate != initial)
                    )
                } catch (e: Throwable) {
                    lastException = e
                    if (isModelUnavailableException(e)) {
                        Log.w(TAG, "Model ${candidate.modelId} unavailable or returned ${e.message}. Trying next candidate...")
                        break // Move immediately to next model without retrying 404/400
                    } else if (isQuotaExceededException(e)) {
                        if (retries < maxRetries) {
                            val backoffDelay = (2.0.pow(retries.toDouble()) * 1000).toLong()
                            Log.w(TAG, "Model ${candidate.modelId} exceeded quota. Retrying in ${backoffDelay}ms... (Retry ${retries + 1}/$maxRetries)")
                            delay(backoffDelay)
                            retries++
                            continue
                        } else {
                            Log.w(TAG, "Model ${candidate.modelId} exhausted retries for quota error. Attempting next candidate...")
                            break // Move to next candidate model
                        }
                    } else {
                        // For other server errors, attempt next candidate in chain before failing
                        Log.w(TAG, "Model ${candidate.modelId} failed with ${e.message}. Attempting next candidate...")
                        break
                    }
                }
            }
        }

        Log.e(TAG, "All models in fallback cascade exhausted or returned errors: ${lastException?.message}")
        return QuotaExecutionResult.Error(
            lastException ?: IOException("Connection error across all available Gemini models"),
            candidates.last()
        )
    }

    fun clearQuotaEvent() {
        _lastQuotaExceededEvent.value = null
    }
}

sealed class QuotaExecutionResult<out T> {
    data class Success<T>(
        val data: T,
        val modelUsed: GeminiModel,
        val wasSwitchedDueToQuota: Boolean
    ) : QuotaExecutionResult<T>()

    data class Error(
        val throwable: Throwable,
        val lastAttemptedModel: GeminiModel
    ) : QuotaExecutionResult<Nothing>()
}
