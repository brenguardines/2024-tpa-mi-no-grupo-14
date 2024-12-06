# syntax = docker/dockerfile:1.2
#
# Build stage
#
FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests


FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
#WORKDIR /app

# Copiar el archivo pom.xml para instalar las dependencias
#COPY pom.xml /app/


# Copiar el archivo .jar generado a la imagen
#COPY target/ejercicio-1.0-SNAPSHOT.jar /app/ejercicio.jar
COPY --from=build target/ejercicio-1.0-SNAPSHOT-jar-with-dependencies.jar ejercicio-1.0-SNAPSHOT.jar
# ENV PORT=8080
EXPOSE 8000
CMD ["java", "-jar", "ejercicio-1.0-SNAPSHOT.jar"]
