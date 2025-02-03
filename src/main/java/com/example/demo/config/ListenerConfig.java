package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import jakarta.jms.*;
import com.example.demo.consumer.ActiveMQConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;



@Component
public class ListenerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ListenerConfig.class);
    private final ActiveMQConfig activeMQConfig;

    private final int numberOfConsumers;
    private final boolean consumerEnabled;
    private final boolean durable;

    public ListenerConfig(ActiveMQConfig activeMQConfig, Environment env) {
        this.activeMQConfig = activeMQConfig;

        // Load properties dynamically
        this.numberOfConsumers = Integer.parseInt(env.getProperty("activemq.consumer.count", "1"));
        this.consumerEnabled = Boolean.parseBoolean(env.getProperty("activemq.consumer.enabled", "true"));
        this.durable = Boolean.parseBoolean(env.getProperty("activemq.consumer.durable", "false"));

        logger.info("Consumer Config Loaded: Consumers={}, Enabled={}, Durable={}", 
                    numberOfConsumers, consumerEnabled, durable);
    }

    @PostConstruct
    public void initializeConsumers() {
        if (!consumerEnabled) {
            logger.info("Consumers are disabled in properties.");
            return;
        }

        try {
            logger.info("Initializing {} consumers...", numberOfConsumers);
            Session session = activeMQConfig.getSession();
            Destination destination = activeMQConfig.getDestination();

            for (int i = 1; i <= numberOfConsumers; i++) {
                MessageConsumer consumer = session.createConsumer(destination);
                ActiveMQConsumer activeMQConsumer = new ActiveMQConsumer(consumer, i);
                Thread consumerThread = new Thread(activeMQConsumer);
                consumerThread.start();
            }

        } catch (JMSException e) {
            logger.error("Error initializing consumers", e);
        }
    }
}
