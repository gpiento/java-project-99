FROM openjdk:21-slim
COPY . .
WORKDIR /
RUN ./gradlew --no-daemon shadowJar
#RUN ./gradlew --no-daemon installDist
ENV JAVA_OPTS "-Xmx512M -Xms512M"
EXPOSE 10000
#CMD ["java", "-jar", "./app/build/libs/app-0.0.1-SNAPSHOT-all.jar"]
CMD ["java", "-jar", "./app/build/libs/app-0.0.1-SNAPSHOT.jar"]
#CMD ["./app/build/install/app/bin/app"]
