FROM gradle:8.1.1-jdk17 AS build

COPY . .

RUN chmod +x gradlew

RUN ./gradlew clean build

FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY --from=build build/libs/alphastock-0.0.1-SNAPSHOT.jar alphastock.jar

EXPOSE 8080

CMD ["java", "-jar", "alphastock.jar"]