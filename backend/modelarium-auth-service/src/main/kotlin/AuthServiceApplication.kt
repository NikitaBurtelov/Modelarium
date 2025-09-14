package org.modelarium.auth

import io.github.cdimascio.dotenv.dotenv
import org.modelarium.auth.config.DotenvPropertySourceInitializer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan()
@EnableJpaRepositories(
    basePackages = ["org.modelarium.auth.persistence.repository"]
)
@SpringBootApplication
class AuthServiceApplication

fun main(args: Array<String>) {
    val application = SpringApplication(AuthServiceApplication::class.java)
    application.addInitializers(DotenvPropertySourceInitializer())

    println("System.getProperty(TG_BOT_KEY) = ${System.getProperty("user.dir")}")
    application.run(*args)
}