# Usar una imagen base con Java
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo pom.xml para instalar las dependencias
COPY pom.xml /app/

# Ejecutar mvn para descargar dependencias
RUN mvn clean install

# Copiar el archivo .jar generado a la imagen
COPY target/ejercicio-1.0-SNAPSHOT.jar /app/ejercicio.jar

# Exponer el puerto que usará la aplicación
EXPOSE 8000

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "/app/ejercicio.jar"]

