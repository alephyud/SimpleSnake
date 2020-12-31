package com.aimomatest.snake

import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener

class GameInterstitialAdListener(
    val interstitialAd: InterstitialAd,
    val onDone: (error: AdError?) -> Unit,
) : InterstitialAdListener {
    private val TAG: String = GameActivity::class.java.getSimpleName()

    override fun onInterstitialDisplayed(ad: Ad?) {
        // Interstitial ad displayed callback
        Log.e(TAG, "Interstitial ad displayed.")
    }

    override fun onInterstitialDismissed(ad: Ad?) {
        // Interstitial dismissed callback
        Log.e(TAG, "Interstitial ad dismissed.")
        onDone(null)
    }

    override fun onError(ad: Ad?, adError: AdError) {
        // Ad error callback
        Log.e(TAG, "Interstitial ad failed to load: " + adError.errorMessage)
        onDone(adError);
    }

    override fun onAdLoaded(ad: Ad?) {
        // Interstitial ad is loaded and ready to be displayed
        Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!")
        // Show the ad
        interstitialAd.show()
    }

    override fun onAdClicked(ad: Ad?) {
        // Ad clicked callback
        Log.d(TAG, "Interstitial ad clicked!")
    }

    override fun onLoggingImpression(ad: Ad?) {
        // Ad impression logged callback
        Log.d(TAG, "Interstitial ad impression logged!")
    }
}