pluginManagement {
    dependencyResolutionManagement {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            google()
            maven { url = uri("https://repo.spring.io/milestone") }
            maven { url = uri("https://repo.spring.io/snapshot") }
        }
    }
    plugins {
        id("org.springframework.boot") version "4.1.0"
        id("io.spring.dependency-management") version "1.1.7"
    }
}

rootProject.name = "backend"
val rootModuleName = "modelarium"

//include("modelarium-auth-service")
include("modelarium-user-service")
include("modelarium-media-service")
include("modelarium-post-service")
include("modelarium-feed-service")
include("modelarium-search-service")
