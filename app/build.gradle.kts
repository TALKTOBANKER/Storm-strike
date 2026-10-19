plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }
  buildToolsVersion = "36.0.0"

  defaultConfig {
    applicationId = "com.stormstrike.app"
    minSdk = 24
    targetSdk = 36
    versionCode = 11
    versionName = "11.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val envKeystore = System.getenv("KEYSTORE_PATH")
      if (envKeystore != null && file(envKeystore).exists()) {
        storeFile = file(envKeystore)
        val sp = System.getenv("STORE_PASSWORD") ?: "123456"
        storePassword = sp
        keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
        keyPassword = System.getenv("KEY_PASSWORD") ?: sp
      } else {
        val localKeystore = file("${rootDir}/stormstrike.jks")
        if (localKeystore.exists()) {
          storeFile = localKeystore
          storePassword = "123456"
          keyAlias = "key0"
          keyPassword = "123456"
        } else {
          val fallback = file("${rootDir}/my-upload-key.jks")
          storeFile = fallback
          val sp = System.getenv("STORE_PASSWORD") ?: "123456"
          storePassword = sp
          keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
          keyPassword = System.getenv("KEY_PASSWORD") ?: sp
        }
      }
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.webkit)
  implementation(libs.play.services.ads)

  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
