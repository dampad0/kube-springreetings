FROM openjdk:17-jdk-slim
ARG appName
ARG version
WORKDIR /app
EXPOSE 8080
COPY build/libs/${appName}-${version}.jar app.jar
CMD ["java", "-jar", "app.jar"]