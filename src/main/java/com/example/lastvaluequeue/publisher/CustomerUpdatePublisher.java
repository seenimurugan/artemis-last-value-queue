package com.example.lastvaluequeue.publisher;

import com.example.lastvaluequeue.domain.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.example.lastvaluequeue.config.QueueConfig.customerUpdateLastValueQueueConfig;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomerUpdatePublisher {

    private final JmsTemplate jmsTemplate;

    private static String[] nameList = {"AAA", "BBB", "CCC", "DDD"};
    private static String[] serviceList = {"SERVICE 1", "SERVICE 2", "SERVICE 3", "SERVICE 4", "SERVICE 5"};

    @Value("${spike.queue-name.customer-update}")
    private String customerUpdateQueueName;

    @Value("${spike.customer-publisher}")
    private boolean isPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void publishCustomer() {

        log.info("is customer-publisher: {}", isPublisher);

        if (isPublisher) {
            IntStream.rangeClosed(1, 100)
                    .forEach(number -> {
                        Random rand = new Random();
                        int value = rand.nextInt(4);
                        String name = nameList[rand.nextInt(nameList.length)];
                        String service = serviceList[rand.nextInt(serviceList.length)];

                        Customer customer = Customer.builder().id(value).name(name).service(service).build();

                        jmsTemplate.convertAndSend(customerUpdateQueueName + customerUpdateLastValueQueueConfig, customer, message -> {
                            message.setIntProperty("Id", customer.getId());
                            return message;
                        });

                        jmsTemplate.convertAndSend(customerUpdateQueueName + customerUpdateLastValueQueueConfig, customer);

                        log.info("Customer message published for customer: {}", customer);

                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
        }

    }
}
