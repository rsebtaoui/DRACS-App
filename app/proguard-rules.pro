# DRACS release ProGuard / R8 rules
# Keep Crashlytics stack traces readable and protect Firestore / Room models.

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# App models & Room entities (reflection / codegen)
-keep class com.khalil.DRACS.Models.** { *; }
-keep class com.khalil.DRACS.Database.** { *; }
-keepclassmembers class com.khalil.DRACS.Database.** { *; }

# Enums used from XML resource IDs (DpaOffice pin mapping)
-keepclassmembers enum com.khalil.DRACS.Models.DpaOffice { *; }

# Firebase / Play services
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}

# Parcelable / Serializable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Resource IDs referenced reflectively / from enums
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**
