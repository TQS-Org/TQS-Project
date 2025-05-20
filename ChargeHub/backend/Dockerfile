FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

RUN chmod 777 -R /app

RUN apt update && apt install -y wget

EXPOSE 8080

CMD mvn spring-boot:run