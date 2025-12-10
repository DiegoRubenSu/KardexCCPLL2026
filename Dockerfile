FROM openjdk:11
COPY target/*.war app.war
CMD ["java", "-jar", "app.war"]