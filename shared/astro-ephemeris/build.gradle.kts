plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        jvmMain.dependencies {
            api(project(":astro-domain"))
            api("org.shredzone.commons:commons-suncalc:3.11")
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}

apply(from = rootProject.file("../../shared/kmp-android-variant.gradle.kts"))
