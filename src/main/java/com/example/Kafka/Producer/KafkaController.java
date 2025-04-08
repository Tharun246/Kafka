package com.example.Kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/kafka")
public class KafkaController
{
    private final KafkaTemplate<String, String> template;

    @Autowired
    public KafkaController(KafkaTemplate<String, String> kafkaTemplate) {
        this.template = kafkaTemplate;
    }

    @GetMapping("/send/{message}")
    public String sendMessage(@PathVariable String message) {
        template.send("my-topic", message);
        return "Message sent successfully!";
    }

    @GetMapping("/hello")
    public String sayHello()
    {
        return "Hello there";
    }

}
