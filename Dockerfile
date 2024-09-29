# Use the official Maven image to build the Spring Boot app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /vendingmachine
# Copy the pom.xml and source code to the container
COPY pom.xml .
COPY src ./src
# Build the Spring Boot app
RUN mvn clean package -D skipTests

# Use an OpenJDK runtime for the final image
FROM openjdk:17-jdk-slim
WORKDIR /vendingmachine
# Copy the Spring Boot jar built from the previous stage
COPY --from=build /vendingmachine/target/*.jar vendingmachine-0.0.1-SNAPSHOT.jar
# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "vendingmachine-0.0.1-SNAPSHOT.jar"]
