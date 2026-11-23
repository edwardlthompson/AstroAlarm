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
            // Android provides org.json at runtime; JVM tests need the artifact.
            compileOnly("org.json:json:20260814")
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")
            implementation("org.json:json:20260814")
        }
    }
}

apply(from = rootProject.file("../../shared/kmp-android-variant.gradle.kts"))
