package app.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaEventConsumer {
    private final KafkaReceiver<String, String> kafkaReceiver;

    public void consume() {
        kafkaReceiver.receive()
                .doOnNext(
                        record -> {
                            //TODO add fun
                            log.info("message {} {}", record.key(), record.value());
                            record.receiverOffset().acknowledge();
                        }
                )
                .doOnError(ex -> log.error("Kafka error: {}", ex.getMessage()))
                .subscribe();
    }
}

