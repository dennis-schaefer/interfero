FROM amazoncorretto:25.0.1-al2023-headless

WORKDIR /app
COPY target/interfero*.jar app.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

