import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    kotlin("native.cocoapods")
}

android {
    namespace = "rlatapy.composeplayground.lib"
    compileSdk = 36
}

kotlin {
    androidTarget()
    iosSimulatorArm64()
    iosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.datadogRum)
            implementation(libs.datadogLogs)
        }
        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }
    }

    cocoapods {
        ios.deploymentTarget = "12.0"
        noPodspec()

        framework {
            baseName = "DatadogKMP"
        }

        pod("DatadogObjc") {
            linkOnly = true
            version = "2.30.1"
        }

        pod("DatadogCrashReporting") {
            linkOnly = true
            version = "2.30.1"
        }
    }

    targets.withType(KotlinNativeTarget::class.java) {
        binaries.all {
            linkerOpts(
                "-framework",
                "CrashReporter",
                "-U",
                "__swift_FORCE_LOAD_\$_swiftCompatibility56",
                "-U",
                "__swift_FORCE_LOAD_\$_swiftCompatibilityConcurrency"
            )
        }
    }
}
