plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.post"
version = rootProject.version

dependencies {
    implementation(libs.spring.web.flux)
    implementation(libs.spring.r2dbc)
    implementation(libs.spring.actuator)
    implementation(libs.spring.docreact)
    developmentOnly(libs.spring.devtools)

    implementation(libs.infra.r2dbc.postgresql)
    implementation(libs.infra.reactor.kafka)

    implementation(libs.util.lombok)
    compileOnly(libs.util.lombok)
    annotationProcessor(libs.util.lombok)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.post.PostServiceApplication")
}

tasks.register("serviceInfo") {
    doLast {
        println("Module: post-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}