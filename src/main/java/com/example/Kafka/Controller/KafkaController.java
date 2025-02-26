package com.example.Kafka.Controller;

import com.example.Kafka.Service.Consumer;
import com.example.Kafka.Service.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/kafka")
public class KafkaController {
    private final Producer producer;
    private final Consumer consumer;

    @Value("${kafka-config.topic}")
    private String topic;

    public KafkaController(Producer producerService, Consumer consumer) {
        this.producer = producerService;
        this.consumer = consumer;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        producer.sendMessage(topic, message);
        return "Message sent to topic " + topic;
    }

    @GetMapping("/messages")
    public List<String> getMessages() {
        return consumer.getMessages();
    }
}
