package app.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Setter
@Getter
public class KafkaProperties {
    private String bootstrapServer;
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Setter
    @Getter
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Setter
    @Getter
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
    }
}
