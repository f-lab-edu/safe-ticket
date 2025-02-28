FROM openjdk:17-jre-slim

WORKDIR /app

COPY build/libs/ticket.jar app.jar

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
