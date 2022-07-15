# Build stage with maven, that's generates our jar
FROM maven:3.8.5-jdk-11 AS Build
ADD . /api-orchestrator
WORKDIR /api-orchestrator
RUN mvn clean package spring-boot:repackage

# Start with a base image containing Java runtime
FROM openjdk:11

# Make port 8080 available to the world outside this container
EXPOSE 8090

COPY --from=build /api-orchestrator/target/api-orchestrator-0.0.1-SNAPSHOT.jar api-orchestrator.jar


# Run the jar file
ENTRYPOINT ["java","-jar","api-orchestrator.jar"]