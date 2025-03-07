FROM openjdk:17-slim

WORKDIR /app

COPY build/libs/ticket-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
