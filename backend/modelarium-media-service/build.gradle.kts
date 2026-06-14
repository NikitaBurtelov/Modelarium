plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.media"
version = rootProject.version

dependencies {
    implementation(libs.spring.web.flux)
    implementation(libs.spring.r2dbc)
    implementation(libs.spring.actuator)
    implementation(libs.spring.docreact)
    developmentOnly(libs.spring.devtools)

    implementation(libs.infra.r2dbc.postgresql)
    implementation(libs.infra.reactor.kafka)
    implementation(libs.infra.s3)

    implementation(libs.util.lombok)
    compileOnly(libs.util.lombok)
    annotationProcessor(libs.util.lombok)
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