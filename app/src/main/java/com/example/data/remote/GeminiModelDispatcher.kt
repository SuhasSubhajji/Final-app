package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GeminiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException

/**
 * Resilient dispatcher that manages Gemini model execution.
 * If quota is exceeded (HTTP 429, RESOURCE_EXHAUSTED, or quota error codes),
 * it automatically falls back sequentially across valid alternative models
 * (e.g., gemini-3.5-flash -> gemini-3.1-flash-lite-preview -> gemini-flash-latest -> gemini-3.1-pro-preview)
 * and alerts observing UI components of the active model change.
 */
object GeminiModelDispatcher {

    private const val TAG = "GeminiModelDispatcher"

    // Text & reasoning fallback cascade priority list
    val fallbackChain = listOf(
        GeminiModel.GEMINI_3_5_FLASH,
        GeminiModel.GEMINI_3_1_FLASH_LITE,
        GeminiModel.GEMINI_FLASH_LATEST,
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
            if (code == 429 || code == 503) return true
            val errorBody = e.response()?.errorBody()?.string().orEmpty().lowercase()
            if (errorBody.contains("quota") || errorBody.contains("resource_exhausted") || errorBody.contains("rate limit")) {
                return true
            }
        }
        val msg = e.message?.lowercase().orEmpty()
        return msg.contains("429") ||
                msg.contains("quota") ||
                msg.contains("resource_exhausted") ||
                msg.contains("rate limit") ||
                msg.contains("rate_limit_exceeded")
    }

    /**
     * Executes the provided Gemini API request. If a quota/rate limit error is caught,
     * it automatically tries the next models in the fallback sequence.
     *
     * @param preferredModel The initial model to attempt (or null to use active model)
     * @param call Lambda executing the API invocation with the candidate model
     * @return Execution result including final model used and output
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
            try {
                Log.d(TAG, "Invoking Gemini with model: ${candidate.modelId}")
                val result = call(candidate)

                // If we successfully executed on a model different from initial due to fallback,
                // update active model and post event
                if (candidate != initial) {
                    val event = QuotaSwitchEvent(
                        originalModel = initial,
                        switchedModel = candidate,
                        message = "Quota limit reached on ${initial.displayName}. Automatically switched to ${candidate.displayName}."
                    )
                    _lastQuotaExceededEvent.value = event
                    _activeModel.value = candidate
                    Log.w(TAG, "Auto-switched model from ${initial.displayName} to ${candidate.displayName} due to quota exhaustion.")
                }

                return QuotaExecutionResult.Success(
                    data = result,
                    modelUsed = candidate,
                    wasSwitchedDueToQuota = (candidate != initial)
                )
            } catch (e: Throwable) {
                lastException = e
                if (isQuotaExceededException(e)) {
                    Log.w(TAG, "Model ${candidate.modelId} exceeded quota (HTTP 429/Resource Exhausted). Attempting next model...")
                    continue
                } else {
                    // Non-quota error (e.g. invalid request format, offline network)
                    Log.e(TAG, "Model ${candidate.modelId} failed with non-quota error: ${e.message}")
                    return QuotaExecutionResult.Error(e, candidate)
                }
            }
        }

        Log.e(TAG, "All models in fallback cascade exhausted or returned errors.")
        return QuotaExecutionResult.Error(
            lastException ?: IOException("Quota exceeded on all available models"),
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
