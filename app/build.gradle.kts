plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "rlatapy.composeplayground"
    compileSdk = 33

    defaultConfig {
        applicationId = "rlatapy.composeplayground"
        minSdk = 30
        targetSdk = 33
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
    composeOptions {
        kotlinCompilerExtensionVersion = "_"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")
    implementation("androidx.activity:activity-compose:_")

    api(platform("dev.chrisbanes.compose:compose-bom:_"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("com.google.accompanist:accompanist-systemuicontroller:_")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // FIXME https://github.com/android/android-test/issues/1755#issuecomment-1511876990
    debugImplementation(AndroidX.Tracing)

    androidTestImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.Compose.ui.testJunit4)
}