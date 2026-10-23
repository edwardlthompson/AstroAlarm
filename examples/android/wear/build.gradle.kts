plugins {
    id("com.android.application")
}

android {
    namespace = "org.astroalarm.wear"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.astroalarm.wear"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // FOSS ONLY: androidx.wear.watchface (Apache-2.0). No Play Services / GMS data layer / HMS.
    implementation("androidx.wear.watchface:watchface:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    testImplementation("junit:junit:4.13.2")
}
