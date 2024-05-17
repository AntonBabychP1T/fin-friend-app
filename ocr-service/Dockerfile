FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/*.jar app.jar
COPY src/main/resources/linear-analyst-423505-s6-4673cc9a4ac9.json /app/service-account-file.json

ENV GOOGLE_APPLICATION_CREDENTIALS="/app/service-account-file.json"

ENTRYPOINT ["java","-jar","/app/app.jar"]
