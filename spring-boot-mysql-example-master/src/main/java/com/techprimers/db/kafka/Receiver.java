package com.techprimers.db.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    @KafkaListener(topics = "${app.topic.foo}")
    public void listen(@Payload String message) {
        System.out.println(message);
        LOG.info("received message='{}'", message);
    }

}
