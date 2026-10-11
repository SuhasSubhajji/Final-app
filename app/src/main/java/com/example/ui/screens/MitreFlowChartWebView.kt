package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberTheme

class MitreAndroidBridge(
    private val context: Context,
    private val onTechniqueSelected: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun copyText(text: String, label: String) {
        mainHandler.post {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Ignore safe fallback
            }
        }
    }

    @JavascriptInterface
    fun onTechniqueSelected(techniqueId: String) {
        mainHandler.post {
            try {
                onTechniqueSelected(techniqueId)
            } catch (e: Exception) {
                // Ignore safe fallback
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MitreFlowChartWebView(
    modifier: Modifier = Modifier,
    initialFocusMode: String = "ALL",
    onTechniqueSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val htmlContent = remember(initialFocusMode) { MitreFlowChartHtmlBuilder.buildHtml(initialFocusMode) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberTheme.colors.bg)
            .testTag("mitre_flow_chart_webview")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(0xFF0A0F1D.toInt())
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowContentAccess = false
                        allowFileAccess = false
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                            val url = request?.url?.toString() ?: return false
                            // Only allow internal data or local app scheme
                            return !(url.startsWith("https://cyber-mitre-app.local/") || url.startsWith("data:"))
                        }
                    }
                    addJavascriptInterface(
                        MitreAndroidBridge(ctx, onTechniqueSelected),
                        "AndroidBridge"
                    )
                    loadDataWithBaseURL(
                        "https://cyber-mitre-app.local/",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    "https://cyber-mitre-app.local/",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}
