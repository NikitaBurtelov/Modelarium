plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.user"
version = rootProject.version

dependencies {
    implementation(rootProject.extra["kotlinCoroutines"] as String)

    implementation(rootProject.extra["springBootStarterDataJpa"] as String)
    implementation(rootProject.extra["springBootStarterLogging"] as String)

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation(rootProject.extra["postgresql"] as String)
    runtimeOnly(rootProject.extra["postgresql"] as String)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.user.MediaServiceApplicationKt")
}

tasks.register("serviceInfo") {
    doLast {
        println("Module: auth-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}
