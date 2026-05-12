FROM node:20-alpine AS frontend-build
WORKDIR /build
COPY frontend/package*.json ./frontend/
RUN cd frontend && npm ci --only=production
COPY frontend/ ./frontend/
RUN cd frontend && npm run build

FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src

COPY --from=frontend-build /build/frontend/build ./src/main/resources/static
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /build/target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]