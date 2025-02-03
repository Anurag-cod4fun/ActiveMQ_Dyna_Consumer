package com.example.demo.controller;

import jakarta.jms.*;
import com.example.demo.config.ActiveMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer")
public class MessageProducerController {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducerController.class);
    private final ActiveMQConfig activeMQConfig;

    public MessageProducerController(ActiveMQConfig activeMQConfig) {
        this.activeMQConfig = activeMQConfig;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        logger.info("Producer: Sending message...");

        try {
            Session session = activeMQConfig.getSession();
            MessageProducer producer = activeMQConfig.getProducer();

            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
            logger.info("Producer: Message sent: {}", message);

            return "Message sent: " + message;
        } catch (JMSException e) {
            logger.error("Producer: Error sending message", e);
            return "Failed to send message";
        }
    }
}
