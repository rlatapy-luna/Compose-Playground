plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.realm)
}

android {
    namespace = "rlatapy.composeplayground"
    compileSdk = 34

    defaultConfig {
        applicationId = "rlatapy.composeplayground"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "benchmark-proguard-rules.pro")
        }
        getByName("debug") {
            //            isMinifyEnabled = true
            //            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "benchmark-proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)

    api(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.realm.kotlin)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}