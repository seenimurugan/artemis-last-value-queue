package com.example.lastvaluequeue.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@RequiredArgsConstructor
public class QueueConfig {

    public static String customerUpdateLastValueQueueConfig = "?last-value-key=Id&non-destructive=true";
    public static String eventUpdateLastValueQueueConfig = "?last-value-key=Id";

    @Value("${spring.artemis.host}")
    private String artemisBrokerHost;
    @Value("${spring.artemis.port}")
    private String artemisBrokerPort;
    @Value("${spring.artemis.user}")
    private String artemisUsername;
    @Value("${spring.artemis.password}")
    private String artemisPassword;

    @Bean
    public ActiveMQConnectionFactory artemisConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + artemisBrokerHost + ":" + artemisBrokerPort);
        connectionFactory.setUser(artemisUsername);
        connectionFactory.setPassword(artemisPassword);
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory artemisCachingConnectionFactory() {
        return new CachingConnectionFactory(artemisConnectionFactory());
    }

    @Bean
    public JmsTemplate artemisTopicJmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(artemisCachingConnectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(false);
        return template;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory artemisTopicJmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(artemisCachingConnectionFactory());
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(artemisCachingConnectionFactory());
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

}
