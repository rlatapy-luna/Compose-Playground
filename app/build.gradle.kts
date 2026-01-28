import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "rlatapy.composeplayground"
    compileSdk = 36

    defaultConfig {
        applicationId = "rlatapy.composeplayground"
        minSdk = 30
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)

    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.accompanist.systemuicontroller)

    implementation(projects.lib)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    testImplementation(libs.kotlinTest)
    testImplementation(libs.kotlinCoroutineTest)

    androidTestImplementation(libs.kotlinTest)
    androidTestImplementation(libs.androidxComposeUiTestJunit)
}