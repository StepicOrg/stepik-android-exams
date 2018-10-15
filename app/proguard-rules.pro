# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------

#google-services
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

#Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

## butterknife
-keepnames class * { @butterknife.BindDrawable *;}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## Joda Time 2.3

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

##---------------Begin: proguard configuration for Retrofit 2  ----------
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

##---------------End: proguard configuration for Retrofit 2  ----------

# android support
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# tests
-dontwarn android.test.**
-dontwarn java.lang.management.**

# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Keep POJO
-keep class org.stepik.android.exams.data.model.** { *; }
-keep interface org.stepik.android.exams.data.model.** { *; }

-keep class org.stepik.android.exams.api.** { *; }
-keep interface org.stepik.android.exams.api.** { *; }
-dontwarn org.stepik.android.exams.api.**
-dontwarn org.stepik.android.exams.data.model.**

-keep class org.stepik.android.model.** { *; }
-keep interface org.stepik.android.model.** { *; }
-keep public enum org.stepik.android.**{ *;}

#Keep all enums
-keep public enum org.stepik.android.exams.**{
    *;
}

#keep javascript interfaces
-keepattributes JavascriptInterface
-keep public class org.stepic.exams.ui.custom.LatexSupportableWebView$OnScrollWebListener
-keepclassmembers class org.stepic.exams.ui.custom.LatexSupportableWebView$OnScrollWebListener {
    public *;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#keep configs names
-keep class org.stepik.android.exams.configuration.** { *; }
-keep interface org.stepik.android.exams.configuration.** { *; }
-dontwarn org.stepik.android.exams.configuration.**

#kotlin
-dontwarn kotlin.**

