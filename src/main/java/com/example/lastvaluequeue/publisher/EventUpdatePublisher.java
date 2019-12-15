package com.example.lastvaluequeue.publisher;

import com.example.lastvaluequeue.domain.Event;
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

import static com.example.lastvaluequeue.config.QueueConfig.eventUpdateLastValueQueueConfig;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventUpdatePublisher {

    private final JmsTemplate jmsTemplate;

    private static String[] nameList = {"AAA", "BBB", "CCC", "DDD", "EEE", "FFF"};
    private static String[] categoryList = {"CA1", "CA2", "CA3", "CA4", "CA5"};


    @Value("${spike.queue-name.event-update}")
    private String eventUpdateQueueName;

    @Value("${spike.event-publisher}")
    private boolean isPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void publishEvent() {

        log.info("is event-publisher: {}", isPublisher);

        if (isPublisher) {
            IntStream.rangeClosed(1, 100)
                    .forEach(number -> {
                        Random rand = new Random();
                        int value = rand.nextInt(5);
                        String name = nameList[rand.nextInt(nameList.length)];
                        String category = categoryList[rand.nextInt(categoryList.length)];

                        Event event = Event.builder().id(value).name(name).category(category).build();

                        jmsTemplate.convertAndSend(eventUpdateQueueName + eventUpdateLastValueQueueConfig, event, message -> {
                            message.setIntProperty("Id", event.getId());
                            return message;
                        });

                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        jmsTemplate.convertAndSend(eventUpdateQueueName + eventUpdateLastValueQueueConfig, event);

                        log.info("Event message published for event: {}", event);

                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
        }

    }

}
