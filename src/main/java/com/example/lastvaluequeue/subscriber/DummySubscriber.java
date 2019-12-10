package com.example.lastvaluequeue.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummySubscriber implements JmsListenerConfigurer {

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId("dummy");
            endpoint.setDestination("dummy");
            endpoint.setMessageListener(customer -> {
                log.info("Customer update received: {}", customer);
            });
            registrar.registerEndpoint(endpoint);
    }
}
