# Primera etapa: Build
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo el pom.xml primero (para cache de dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Segunda etapa: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Comando de inicio - ¡ESTA ES LA CLAVE!
ENTRYPOINT ["java", "-jar", "/app/app.jar"]