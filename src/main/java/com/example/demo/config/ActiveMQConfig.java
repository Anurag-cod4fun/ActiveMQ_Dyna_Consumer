package com.example.demo.config;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConfig.class);

    private final String brokerUrl;
    private final String destinationType;
    private final String destinationName;
    private final String acknowledgeMode;

    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public ActiveMQConfig(Environment env) {
        this.brokerUrl = env.getProperty("activemq.broker.url", "tcp://localhost:61616");
        this.destinationType = env.getProperty("activemq.destination.type", "queue");
        this.destinationName = env.getProperty("activemq.destination.name", "default-queue");
        this.acknowledgeMode = env.getProperty("activemq.acknowledge.mode", "AUTO_ACKNOWLEDGE");

        logger.info("Loaded Config: Broker URL={}, Destination Type={}, Destination Name={}",
                brokerUrl, destinationType, destinationName);

        initializeConnection();
    }

    private void initializeConnection() {
        try {
            logger.info("Connecting to ActiveMQ at {}", brokerUrl);
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            connection = connectionFactory.createConnection();
            connection.start();
            logger.info("ActiveMQ Connection established.");

            int ackMode = Session.AUTO_ACKNOWLEDGE;
            if ("CLIENT_ACKNOWLEDGE".equalsIgnoreCase(acknowledgeMode)) {
                ackMode = Session.CLIENT_ACKNOWLEDGE;
            }

            session = connection.createSession(false, ackMode);

            if ("topic".equalsIgnoreCase(destinationType)) {
                destination = session.createTopic(destinationName);
                logger.info("Using Topic: {}", destinationName);
            } else {
                destination = session.createQueue(destinationName);
                logger.info("Using Queue: {}", destinationName);
            }

            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            logger.info("Message Producer initialized.");

        } catch (JMSException e) {
            logger.error("Error initializing ActiveMQ", e);
        }
    }

    public Session getSession() {
        return session;
    }

    public Destination getDestination() {
        return destination;
    }

    public MessageProducer getProducer() {
        return producer;
    }
}
