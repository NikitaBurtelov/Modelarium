plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.user"
version = rootProject.version

dependencies {
    implementation(rootProject.extra["springBootStarterWeb"] as String)
    implementation(rootProject.extra["SpringBootStarterWebFlux"] as String) //webClient
    implementation(rootProject.extra["springBootStarterActuator"] as String)
    implementation(rootProject.extra["springBootStarterLogging"] as String)
    implementation(rootProject.extra["springBootStarterDataJpa"] as String)
    implementation(rootProject.extra["springBootStarterLogging"] as String)
    implementation(rootProject.extra["springBootStarterDataJdbc"] as String)
    implementation(rootProject.extra["springBootStarterJdbc"] as String)
    implementation(rootProject.extra["springBootStarterValidation"] as String)
    developmentOnly(rootProject.extra["springBootDevtools"] as String)

    implementation(rootProject.extra["lombok"] as String)
    compileOnly(rootProject.extra["lombok"] as String)
    annotationProcessor(rootProject.extra["lombok"] as String)

    implementation(rootProject.extra["postgresql"] as String)
    runtimeOnly(rootProject.extra["postgresql"] as String)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.user.UserServiceApplication")
}

tasks.register("serviceInfo") {
    doLast {
        println("Module: user-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}
