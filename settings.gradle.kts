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
    }
}

rootProject.name = "AirApp"
include(":app")
include(":core")
include(":feature:day-1-chat")
include(":feature:day-2-format")
include(":feature:day-3-reasoning")
include(":feature:day-4-temperature")
include(":feature:day-5-models")
