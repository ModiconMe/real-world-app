FROM openjdk:17-jdk-alpine
COPY build/libs/real-world-example-1.0.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "real-world-example-1.0.jar"]