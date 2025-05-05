package com.khalil.DRACS;

import com.google.firebase.testlab.testing.FirebaseTestLabConfig;

public class FirebaseTestConfig {
    public static final FirebaseTestLabConfig CONFIG = new FirebaseTestLabConfig.Builder()
            .setDeviceModel("Pixel_4")  // Using Pixel 4 instead of Pixel2
            .setAndroidVersion("30")    // Android 11
            .setOrientation("portrait")
            .setLocale("en_US")
            .build();
} 