FROM maven:3.8.4-openjdk-11-slim

ADD . /app
WORKDIR /app

RUN mvn clean package

EXPOSE 8080
CMD ["java", "-jar", "target/primus-learning-0.0.1-SNAPSHOT.jar"]
