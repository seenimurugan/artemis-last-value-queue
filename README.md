# artemis-last-value-queue

###Run as customer publisher:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--customer-publisher=true

###Run as customer subscriber:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--customer-consumer=true

---

###Run as Event publisher:
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--event-publisher=true

###Run as Event subscriber
> mvn clean spring-boot:run -Dspring-boot.run.arguments=--event-consumer=true