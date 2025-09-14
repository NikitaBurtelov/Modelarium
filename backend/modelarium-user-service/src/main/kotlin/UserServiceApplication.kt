package org.modelarium.user

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

//@ComponentScan(
//    basePackages = [""]
//)
@SpringBootApplication
class UserServiceApplication

fun main(args: Array<String>) {
    dotenv {
        filename = ".env"
        ignoreIfMalformed = true
        ignoreIfMissing = true
        systemProperties = true
    }
    runApplication<UserServiceApplication>()
}