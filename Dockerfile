FROM openjdk:11-jdk-slim
WORKDIR /app
COPY . .
RUN chmod +x mvnw 2>/dev/null || true
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests
CMD ["java", "-jar", "target/*.jar"]