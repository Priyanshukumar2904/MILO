# Milo ProGuard / R8 Rules for Production Minification
-keepattributes *Annotation*
-keepattributes Signature
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class com.milo.app.domain.models.** { *; }
