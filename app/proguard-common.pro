# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose

-keep class androidx.core.app.* { *; }
-keep interface androidx.core.app.* { *; }

#-keep public class bomi.kotlinside.api.data.* { *; }
-keep public class bomi.kotlinside.api.req.* { *; }
-keep public class bomi.kotlinside.api.res.* { *; }
-keep public class bomi.kotlinside.api.deserialization.* { *; }
#-dontwarn bomi.kotlinside.api.data.**
-dontwarn bomi.kotlinside.api.request.**
-dontwarn bomi.kotlinside.api.response.**
-dontwarn bomi.kotlinside.api.deserialization.**

#-keep public class bomi.kotlinside.ui.data.* { *; }
#-dontwarn emforce.goodchobo.car.ui.data.**

-keep interface org.apache.http.*
-keep class org.apache.http.* {public *;}

-keep public class bomi.kotlinside.util.CommonUtil { *; }

# Firebase Authentication
-keepattributes *Annotation*
# Firebase Realtime database
-keepattributes Signature

#Kotlin
-keep class kotlin.* { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# KOIN
-keepnames class androidx.lifecycle.ViewModel
-keepclassmembers public class * extends androidx.lifecycle.ViewModel { public <init>(...); }
-keepclassmembers class com.lebao.app.domain.* { public <init>(...); }
-keepclassmembers class * { public <init>(...); }
