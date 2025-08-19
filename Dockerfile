FROM maven:3.8.6-openjdk-18-slim

ADD . /app
WORKDIR /app

RUN mvn clean package

EXPOSE 8080
CMD ["java", "-jar", "target/primus-learning-0.0.1-SNAPSHOT.jar"]

