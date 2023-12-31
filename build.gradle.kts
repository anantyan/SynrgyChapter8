// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.android.library") version "8.1.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("land.sungbin.dependency.graph.plugin") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

dependencyGraphConfig {
    projectName = "dependencies-graph-synrgychapter7"
    outputFormat = OutputFormat.SVG
    dependencyBuilder {
        null
    }
}

subprojects {
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask> {
        reportsOutputDirectory.set(
            project.layout.buildDirectory.dir("reports-ktlint/$name")
        )
    }
}
