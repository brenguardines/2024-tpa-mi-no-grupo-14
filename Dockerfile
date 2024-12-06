# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Define el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR construido en el contenedor
COPY target/ejercicio-1.0-SNAPSHOT.jar /app/ejercicio.jar

# Expón el puerto en el que se ejecuta la aplicación
EXPOSE 8000

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "/app/ejercicio.jar"]

