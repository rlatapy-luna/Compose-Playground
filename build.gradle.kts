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

dependencies {
    detektPlugins(libs.detekt.rules.ktlint.wrapper)
}

detekt {
    source.setFrom(layout.projectDirectory.file("app/src/main/kotlin/rlatapy/composeplayground/detektcode"))
    config.setFrom(layout.projectDirectory.file("custom-detekt-config.yml"))
    autoCorrect = true
    buildUponDefaultConfig = true
    ignoreFailures = true
}

tasks.withType<dev.detekt.gradle.Detekt> {
    outputs.upToDateWhen { false }
}