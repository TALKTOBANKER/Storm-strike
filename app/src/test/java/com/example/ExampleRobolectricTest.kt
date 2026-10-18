package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun testAppNameAndAdMobStrings() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("STORMSTRIKE", appName)

    val appId = context.getString(R.string.admob_app_id)
    assertEquals("ca-app-pub-3785297962335569~7993443539", appId)

    val bannerId = context.getString(R.string.admob_banner_id)
    assertEquals("ca-app-pub-3785297962335569/4331821111", bannerId)

    val interstitialId = context.getString(R.string.admob_interstitial_id)
    assertEquals("ca-app-pub-3785297962335569/2488256404", interstitialId)
  }

  @Test
  fun testAssetsExist() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val assetManager = context.assets
    val assetsList = assetManager.list("") ?: emptyArray()
    assertTrue(assetsList.contains("index.html"))
    assertTrue(assetsList.contains("three.min.js"))
    assertTrue(assetsList.contains("logo.png"))
  }

  @Test
  fun testLaunchMainActivity() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    org.junit.Assert.assertNotNull(activity)
  }
}
