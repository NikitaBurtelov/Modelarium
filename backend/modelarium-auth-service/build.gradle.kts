plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.auth"
version = rootProject.version

dependencies {
    implementation(rootProject.extra["kotlinCoroutines"] as String)

    implementation(rootProject.extra["springBootStarterSecurity"] as String)
    implementation(rootProject.extra["springBootStarterDataJpa"] as String)
    implementation(rootProject.extra["springBootStarterLogging"] as String)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation(rootProject.extra["jackson"] as String)

    implementation(rootProject.extra["jjwtApi"] as String)
    runtimeOnly(rootProject.extra["jjwtImpl"] as String)
    runtimeOnly(rootProject.extra["jjwtJackson"] as String)

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(rootProject.extra["postgresql"] as String)
    runtimeOnly(rootProject.extra["postgresql"] as String)

    // Flyway (relies on Spring Boot BOM for version)
    //implementation("org.flywaydb:flyway-core")

    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("org.testcontainers:postgresql:1.19.0")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.auth.AuthServiceApplicationKt")
}

tasks.register("serviceInfo") {
    doLast {
        println("Module: auth-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}