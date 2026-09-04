FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
