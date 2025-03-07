FROM openjdk:17-slim

WORKDIR /app

COPY build/libs/ticket.jar app.jar

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
