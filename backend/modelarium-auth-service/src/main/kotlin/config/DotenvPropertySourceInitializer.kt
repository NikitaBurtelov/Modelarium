package org.modelarium.auth.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

class DotenvPropertySourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(ctx: ConfigurableApplicationContext) {
        val dotenv = Dotenv.configure()
            .directory("${System.getProperty("user.dir")}/backend/modelarium-auth-service")
            .filename(".env")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load()

        val map = dotenv.entries().associate { it.key to it.value }
        ctx.environment.propertySources.addFirst(MapPropertySource("dotenvProperties", map))
    }
}
