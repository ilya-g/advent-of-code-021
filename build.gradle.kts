import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "org.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDir("src")
    }
    test {
        java.srcDir("test")
    }
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.time.ExperimentalTime")
//            optIn("kotlin.io.path.ExperimentalPathApi")
            optIn("kotlin.ExperimentalStdlibApi")
        }
    }
}

