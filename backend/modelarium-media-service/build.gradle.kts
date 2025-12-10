plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.media"
version = rootProject.version

dependencies {
    implementation(rootProject.extra["springBootStarterLogging"] as String)
    implementation(rootProject.extra["SpringBootStarterWebFlux"] as String)
    implementation(rootProject.extra["SpringBootStarterR2dbc"] as String)
    implementation(rootProject.extra["r2dbcPostgresql"] as String)
    implementation(rootProject.extra["reactKafka"] as String)
    implementation(rootProject.extra["s3"] as String)
    implementation(rootProject.extra["lombok"] as String)
    compileOnly(rootProject.extra["lombok"] as String)
    annotationProcessor(rootProject.extra["lombok"] as String)
    developmentOnly(rootProject.extra["springBootDevtools"] as String)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.media.MediaServiceApplication")
}

tasks.register("serviceInfo") {
    doLast {
        println("Module: media-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}