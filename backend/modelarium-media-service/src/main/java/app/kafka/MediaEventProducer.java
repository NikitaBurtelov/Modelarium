package app.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaEventProducer {
    private final KafkaSender<String, String> kafkaSender;

    //TODO add fun
    public Mono<SenderResult<String>> sendMessage(String topic, String key, String value) {
        SenderRecord<String, String, String> record =
                SenderRecord.create(new ProducerRecord<>(topic, key, value), key);

        return kafkaSender.send(Mono.just(record)).next();
    }
}

