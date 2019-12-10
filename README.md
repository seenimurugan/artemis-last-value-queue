# artemis-last-value-queue

###Run Artemis:
> docker run --name='artemis' -it --rm \\\
    -e ARTEMIS_USERNAME=capp \\\
    -e ARTEMIS_PASSWORD=admin \\\
    -e 'ARTEMIS_MIN_MEMORY=1024M' \\\
    -e 'ARTEMIS_MAX_MEMORY=1024M' \\\
    -p 8161:8161 \\\
    -p 61616:61616 \\\
    -p 61613:61613 \\\
    vromero/activemq-artemis

###Run as customer publisher:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--customer-publisher=true

###Run as customer subscriber:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--customer-consumer=true

---

###Run as Event publisher:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--event-publisher=true

###Run as Event subscriber
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--event-consumer=true