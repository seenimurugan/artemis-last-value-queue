package com.example.lastvaluequeue.subscriber;

import com.example.lastvaluequeue.domain.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventSubscriber implements JmsListenerConfigurer {

    private final ObjectMapper objectMapper;

    @Value("${spike.event-consumer}")
    private boolean isEventConsumer;

    @Value("${spike.queue-name.event-update}")
    private String eventUpdateQueueName;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {

        log.info("isEventConsumer: {}", isEventConsumer);

        if (isEventConsumer) {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId("eventUpdateQueueName");
            endpoint.setDestination(eventUpdateQueueName);
            endpoint.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        var msg = ((TextMessage) message).getText();
                        var event = objectMapper.readValue(msg, Event.class);
                        log.info("Event update received: {}", event);
                    } catch (JMSException | JsonProcessingException ex) {
                        throw new IllegalStateException(ex);
                    }
                } else {
                    throw new IllegalArgumentException("Message must be of type TextMessage");
                }
            });

            registrar.registerEndpoint(endpoint);

            log.info("Started listening to event update");
        }
    }

}
