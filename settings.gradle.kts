enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "linphone.org maven repository"
            url = uri("https://download.linphone.org/maven_repository")
            content {
                includeGroup("org.linphone")
            }
        }
    }
}

rootProject.name = "phoneapp"
include(":androidApp")
include(":shared")