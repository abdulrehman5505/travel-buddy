pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml")) // 👈 Required to load your TOML
        }
    }
}

rootProject.name = "TravelBuddy"
include(":app")
