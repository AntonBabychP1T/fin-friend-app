package pitdeveloping.ocrservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OcrListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcrListener.class);

    @KafkaListener(topics = "telegram-bot-topic", groupId = "ocr-service-group")
    public void listen(String message) {
        LOGGER.info("Received message: {}", message);
        System.out.println(message);
    }
}
