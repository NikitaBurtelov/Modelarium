plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    kotlin("plugin.allopen") version "2.1.21"
    kotlin("plugin.noarg") version "2.1.21"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
}
group = "org.modelarium"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

extra.apply {
    set("kotlinStdlib", "org.jetbrains.kotlin:kotlin-stdlib")
    set("kotlinReflect", "org.jetbrains.kotlin:kotlin-reflect")
    set("kotlinCoroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    set("springBootStarterActuator", "org.springframework.boot:spring-boot-starter-actuator")
    set("springBootStarterWeb", "org.springframework.boot:spring-boot-starter-web")
    set("springBootStarterDataJpa", "org.springframework.boot:spring-boot-starter-data-jpa")
    set("springBootStarterLogging", "org.springframework.boot:spring-boot-starter-logging")
    set("springBootStarterSecurity", "org.springframework.boot:spring-boot-starter-security")

    set("postgresql", "org.postgresql:postgresql:42.7.3")

    set("jackson", "com.fasterxml.jackson.module:jackson-module-kotlin")

    set("jjwtApi", "io.jsonwebtoken:jjwt-api:0.11.5")
    set("jjwtImpl", "io.jsonwebtoken:jjwt-impl:0.11.5")
    set("jjwtJackson", "io.jsonwebtoken:jjwt-jackson:0.11.5")

    set("dotenv", "io.github.cdimascio:dotenv-kotlin:6.4.1")
    set("jsoup", "org.jsoup:jsoup:1.17.2")

    set("springKafka", "org.springframework.kafka:spring-kafka:3.2.2")
    set("springRetry", "org.springframework.retry:spring-retry:2.0.3")
    set("springDoc", "org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    set("kotlinLogging", "io.github.microutils:kotlin-logging-jvm:3.0.5")

    set("springBootStarterTest", "org.springframework.boot:spring-boot-starter-test")
    set("junitJupiter", "org.junit.jupiter:junit-jupiter:5.11.0")
    set("mockitoCore", "org.mockito:mockito-core:5.12.0")
    set("mockitoKotlin", "org.mockito.kotlin:mockito-kotlin:5.3.1")
}

configure(subprojects) {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }

    apply(plugin = "org.springframework.boot")
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "idea")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin-spring")
    apply(plugin = "java-library")

    dependencies {
        implementation(rootProject.extra["kotlinStdlib"] as String)
        implementation(rootProject.extra["kotlinReflect"] as String)

        implementation(rootProject.extra["springBootStarterActuator"] as String)
        implementation(rootProject.extra["springBootStarterWeb"] as String)
        implementation(rootProject.extra["springBootStarterDataJpa"] as String)
        implementation(rootProject.extra["springBootStarterLogging"] as String)

        implementation(rootProject.extra["dotenv"] as String)

        implementation(rootProject.extra["springDoc"] as String)

        implementation(rootProject.extra["kotlinLogging"] as String)

        testImplementation(rootProject.extra["springBootStarterTest"] as String)
        testImplementation(rootProject.extra["junitJupiter"] as String)
        testImplementation(rootProject.extra["mockitoCore"] as String)
        testImplementation(rootProject.extra["mockitoKotlin"] as String)
    }
}