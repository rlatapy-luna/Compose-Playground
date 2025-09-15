plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

android {
    namespace = "rlatapy.composeplayground.libtwo"
    compileSdk = 36
}

kotlin {
    androidTarget()
    iosSimulatorArm64()
    iosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.lib)
        }
        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }
    }
}
