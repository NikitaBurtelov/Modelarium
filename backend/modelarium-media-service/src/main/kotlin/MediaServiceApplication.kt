package org.modelarium

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@ComponentScan(
//    basePackages = [""]
//)
@SpringBootApplication
class MediaServiceApplication

fun main(args: Array<String>) {
    dotenv {
        filename = ".env"
        ignoreIfMalformed = true
        ignoreIfMissing = true
        systemProperties = true
    }
    runApplication<MediaServiceApplication>()
}