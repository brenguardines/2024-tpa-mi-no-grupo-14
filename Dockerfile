# Etapa de construcción
FROM openjdk:17-slim AS build
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/ejercicio-1.0-SNAPSHOT.jar /ejercicio-1.0-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "/ejercicio-1.0-SNAPSHOT.jar"]
