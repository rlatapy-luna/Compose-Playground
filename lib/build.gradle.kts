plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm()
    iosSimulatorArm64()
    iosArm64()
}
