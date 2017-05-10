# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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


-keep class com.med.fast.** {*;}
-dontwarn com.med.fast.**

# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Keep the names of classes/members we need for client functionality.
-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Needed when building against pre-Marshmallow SDK.
-dontwarn android.security.NetworkSecurityPolicy

# Keep metadata about included modules.
-keep public class com.google.android.gms.dynamite.descriptors.** {
  public <fields>;
}

# Keep the implementation of the flags api for google-play-services-flags

-keep public class com.google.android.gms.flags.impl.FlagProviderImpl {
  public <fields>; public <methods>;
}

-keepattributes InnerClasses, EnclosingMethod
#-keep class com.makeramen.roundedimageview** { *; }
# Ensure annotations are kept for runtime use.
-keepattributes *Annotation*
# Don't remove any GreenRobot classes
-keepnames class org.greenrobot.** {*;}
# Don't remove any methods that have the @Subscribe annotation
-keepclassmembers class ** {
    @de.greenrobot.event.Subscribe <methods>;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.** { *; }
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
# default
-keep class com.sample.package.models.** { *; } # sample only cant public package right now
##---------------End: proguard configuration for Gson  ----------

-keep class com.google.gson
-keep class Gson**
-keepclassmembers class Gson** {
    *;
}
## OkHttp
-keep class okhttp3.internal.** { *; }
-dontwarn okhttp3.internal.**
## okio?
-keep class java.nio.file.** { *; }
-dontwarn java.nio.file.**
-keep class org.codehaus.mojo.animal_sniffer.** { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.**

## Android Graphics
-keep class org.xmlpull.v1.** { *; }
-dontwarn org.xmlpull.v1.**

## ButterKnife
-keep class **$$ViewBinder { *; }

## RXJava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

## Gravity Snap Helper
-keep class com.github.rubensousa.** {*;}
-dontwarn com.github.rubensousa.**

## RealmField name helper
-keep class dk.ilios.** {*;}
-dontwarn dk.ilios.**

## Joda Time
-keep class org.joda.** {*;}
-dontwarn org.joda.**