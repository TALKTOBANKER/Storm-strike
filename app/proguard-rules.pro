# ProGuard / R8 Rules for STORMSTRIKE

# Preserve JavaScript Interface methods used by WebView
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve the inner AndroidBridge class
-keep class com.example.MainActivity$AndroidBridge {
    <methods>;
}

# Google Mobile Ads (AdMob) keep rules
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep class com.google.ads.** {
    public *;
}

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
