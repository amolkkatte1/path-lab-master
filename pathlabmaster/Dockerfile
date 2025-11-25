# Stage 1: Maven + Java 21 for building
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pathlabmaster/pom.xml .
COPY pathlabmaster/src ./src

RUN mvn clean package -DskipTests

# Stage 2: Minimal Java runtime to run the JAR
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/pathlabmaster-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

ENV APP_ENV=default

CMD ["java", "-Xmx1024m", "-jar", "/app/app.jar"]