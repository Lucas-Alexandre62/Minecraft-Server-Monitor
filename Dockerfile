FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

RUN chmod +x mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:25-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/minecraft-monitor-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT:-8080}

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
