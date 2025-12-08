package app;

import app.config.properties.DotenvPropertySourceInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
public class MediaServiceApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MediaServiceApplication.class);
        app.addInitializers(new DotenvPropertySourceInitializer());
        app.run(args);
    }
}

