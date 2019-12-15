package com.example.lastvaluequeue.subscriber;

import com.example.lastvaluequeue.domain.Customer;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerSubscriber implements JmsListenerConfigurer {

    private final ObjectMapper objectMapper;

    @Value("${spike.customer-consumer}")
    private boolean isCustomerConsumer;

    @Value("${spike.queue-name.customer-update}")
    private String customerUpdateQueueName;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {

        log.info("isCustomerConsumer: {}", isCustomerConsumer);

        if (isCustomerConsumer) {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId("customerUpdateQueueName");
            endpoint.setDestination(customerUpdateQueueName);
            endpoint.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        var msg = ((TextMessage) message).getText();
                        var customer = objectMapper.readValue(msg, Customer.class);
                        log.info("Customer update received: {}", customer);
                    } catch (JMSException | JsonProcessingException ex) {
                        throw new IllegalStateException(ex);
                    }
                } else {
                    throw new IllegalArgumentException("Message must be of type TextMessage");
                }
            });
            registrar.registerEndpoint(endpoint);
            log.info("Started listening to Customer update");
        }
    }
}
