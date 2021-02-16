#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn

-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
-keepattributes InlinedApi
-keepattributes SourceFile,LineNumberTable
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers class **.*$AddressHelperInterface { *; }
-keepclassmembers class **.*$JavaScriptInterface { *; }
-keep public class **.*$AddressHelperInterface
-keep public class **.*$JavaScriptInterface