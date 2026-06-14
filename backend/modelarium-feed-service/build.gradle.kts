plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "${rootProject.group}.feed"
version = rootProject.version

repositories {
    mavenCentral()
}

tasks.register("serviceInfo") {
    description = ""
    doLast {
        println("Module: feed-service")
        println("Group: ${project.group}, Version: ${project.version}")
    }
}

tasks.test {
    useJUnitPlatform()
}