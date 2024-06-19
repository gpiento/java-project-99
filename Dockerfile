FROM openjdk:21-slim
COPY . .
WORKDIR /app
RUN ./gradlew --no-daemon shadowJar
ENV JAVA_OPTS "-Xmx512M -Xms512M"
EXPOSE 7070
CMD ["java", "-jar", "./build/libs/app-1.0-SNAPSHOT-all.jar"]
