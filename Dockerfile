FROM openjdk:21-slim
ENV JAVA_OPTS "-Xmx512M -Xms512M"
COPY . .
WORKDIR /
RUN ./gradlew build
#EXPOSE 8080:8080
CMD ["java", "-jar", "./app/build/libs/app-0.0.1-SNAPSHOT.jar"]
