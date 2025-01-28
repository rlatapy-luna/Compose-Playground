// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }

    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
//    source.from(files(rootProject.rootDir))
}

tasks.withType<Detekt>().configureEach {
    outputs.upToDateWhen { false } // always re-run

    exclude("**/buildSrc")
    exclude("**/build/**")

    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
    }
}
