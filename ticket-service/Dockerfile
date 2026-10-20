FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml pom.xml
COPY ticket-service/pom.xml ticket-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn -q -pl ticket-service -am dependency:go-offline
COPY ticket-service/src ticket-service/src
RUN mvn -q -pl ticket-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/ticket-service/target/ticket-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
