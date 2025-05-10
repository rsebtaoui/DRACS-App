plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.khalil.DRACS"
    compileSdk = 34

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.khalil.DRACS"
        minSdk = 23
        targetSdk = 34
        versionCode = 3
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // Google Play Services - using the latest stable versions
    implementation("com.google.android.gms:play-services-base:18.7.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.android.gms:play-services-tasks:18.3.0")
    implementation("com.google.android.gms:play-services-basement:18.7.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // AndroidX Core
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    //Buttom app bar dependancy
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    //Drawer layout
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //expand layout
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.cachapa:ExpandableLayout:2.9.2")

    //notification of the app update
    implementation("com.google.android.play:app-update:2.1.0")

    implementation("com.facebook.shimmer:shimmer:0.5.0")
}