// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // AGP 8.13.x is the newest stable 8.x and supports compileSdk 36 (min for API 36 is 8.9.1).
    // Requires Gradle 8.13 and JDK 17.
    id("com.android.application") version "8.13.2" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}