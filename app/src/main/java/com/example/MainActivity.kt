package com.example

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.webkit.WebViewAssetLoader
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : ComponentActivity() {

    private val TAG = "StormStrike"
    private lateinit var webView: WebView
    private var bannerAdView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var isGamePlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            hideSystemUI()
        } catch (e: Exception) {
            Log.e(TAG, "Error setting window flags", e)
        }

        // Initialize AdMob safely
        try {
            MobileAds.initialize(this) {}
            loadInterstitialAd()
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization failed", e)
        }

        val rootLayout = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF07111C.toInt())
        }

        // Use modern WebViewAssetLoader to eliminate CORS and file:// protocol blocks
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setupWebViewSettings(this)

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d(TAG, "[WebView] ${it.message()} -- line ${it.lineNumber()} of ${it.sourceId()}")
                    }
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(Uri.parse(url))
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    Log.e(TAG, "WebView error: $description at $failingUrl")
                    // Fallback to direct file url if assetLoader ever fails
                    if (failingUrl?.startsWith("https://appassets.androidplatform.net") == true) {
                        view?.loadUrl("file:///android_asset/index.html")
                    }
                }
            }

            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidBridge")

            // Load via assetLoader scheme first, which has full HTTPS privileges & no CORS blocks
            loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }
        rootLayout.addView(webView)

        setupBannerAd(rootLayout)
        setContentView(rootLayout)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isGamePlaying) {
                    webView.evaluateJavascript("if (typeof pauseGame === 'function') pauseGame();", null)
                } else {
                    finish()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewSettings(wv: WebView) {
        wv.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            @Suppress("DEPRECATION")
            allowFileAccessFromFileURLs = true
            @Suppress("DEPRECATION")
            allowUniversalAccessFromFileURLs = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            databaseEnabled = true
        }
        wv.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private fun setupBannerAd(root: FrameLayout) {
        try {
            val bannerId = getString(R.string.admob_banner_id)
            if (bannerId.isNotEmpty()) {
                val adView = AdView(this).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = bannerId
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                    }
                }
                root.addView(adView)
                adView.loadAd(AdRequest.Builder().build())
                bannerAdView = adView
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Banner Ad", e)
        }
    }

    private fun loadInterstitialAd() {
        try {
            val interstitialId = getString(R.string.admob_interstitial_id)
            if (interstitialId.isEmpty()) return

            InterstitialAd.load(
                this,
                interstitialId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                    }
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Interstitial Ad", e)
        }
    }

    fun showInterstitial() {
        runOnUiThread {
            try {
                interstitialAd?.let { ad ->
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            loadInterstitialAd()
                        }
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                            loadInterstitialAd()
                        }
                    }
                    ad.show(this)
                } ?: run {
                    loadInterstitialAd()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error showing Interstitial Ad", e)
            }
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        bannerAdView?.resume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        bannerAdView?.pause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdView?.destroy()
        webView.destroy()
    }

    inner class WebAppInterface(private val activity: Activity) {
        @JavascriptInterface
        fun onGameStateChanged(playing: Boolean) {
            isGamePlaying = playing
            activity.runOnUiThread {
                bannerAdView?.visibility = if (playing) View.GONE else View.VISIBLE
            }
        }

        @JavascriptInterface
        fun showInterstitialAd() {
            showInterstitial()
        }

        @JavascriptInterface
        fun vibrate(milliseconds: Long) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    activity.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(
                            milliseconds.coerceIn(10, 1000),
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(milliseconds.coerceIn(10, 1000))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Vibration failed", e)
            }
        }
    }
}
