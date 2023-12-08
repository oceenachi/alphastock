FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY build/libs/alphastock-0.0.1-SNAPSHOT.jar alphastock.jar

EXPOSE 8080

CMD ["java", "-jar", "alphastock.jar"]