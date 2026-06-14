plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}


group = "${rootProject.group}.user"
version = "${rootProject.version}"

dependencies {
    implementation(libs.spring.web)
    implementation(libs.spring.web.flux)
    implementation(libs.spring.actuator)
    implementation(libs.spring.security)
    implementation(libs.spring.oauth2)
    implementation(libs.spring.doc)
    implementation(libs.spring.redis)
    implementation(libs.spring.validation)
    implementation(libs.spring.jdbc)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.data.jdbc)

    implementation(libs.infra.kafka)

    implementation(libs.util.bucket4j.core)
    implementation(libs.util.bucket4j.lettuce)
    implementation(libs.util.lombok)
    compileOnly(libs.util.lombok)
    annotationProcessor(libs.util.lombok)

    implementation(libs.infra.s3)
    runtimeOnly(libs.infra.postgresql)

    developmentOnly(libs.spring.devtools)

    testImplementation(libs.spring.test)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.modelarium.user.UserServiceApplication")
}

tasks.register("serviceInfo") {
    description = ""
    doLast {
        println("Module: user-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}