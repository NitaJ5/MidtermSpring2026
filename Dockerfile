FROM eclipse-temurin:17

WORKDIR /app

COPY target/midterm-spring-2026-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]