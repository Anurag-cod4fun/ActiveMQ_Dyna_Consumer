package com.example.demo.consumer;

import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQConsumer implements MessageListener, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConsumer.class);
    private final MessageConsumer consumer;
    private final int consumerId;

    public ActiveMQConsumer(MessageConsumer consumer, int consumerId) throws JMSException {
        this.consumer = consumer;
        this.consumer.setMessageListener(this);
        this.consumerId = consumerId;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                logger.info("Consumer-{}: Received message: {}", consumerId, textMessage.getText());
            }
        } catch (JMSException e) {
            logger.error("Consumer-{}: Error processing message", consumerId, e);
        }
    }

    @Override
    public void run() {
        logger.info("Consumer-{}: Listening for messages...", consumerId);
        while (true) {}
    }
}
