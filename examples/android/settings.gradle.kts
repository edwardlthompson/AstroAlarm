pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "astroalarm"
include(":app")
include(":wear")
include(":astro-domain")
include(":astro-ephemeris")
include(":astro-ui")
project(":astro-domain").projectDir = file("../../shared/astro-domain")
project(":astro-ephemeris").projectDir = file("../../shared/astro-ephemeris")
project(":astro-ui").projectDir = file("../../shared/astro-ui")
