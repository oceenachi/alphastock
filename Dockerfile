FROM gradle:8.1.1-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src/producer

WORKDIR /home/gradle/src/producer

RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY --from=build /home/gradle/src/producer/build/libs/*.jar alphastock.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "alphastock.jar"]