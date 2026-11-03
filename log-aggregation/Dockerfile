FROM internal.example.com/hub.docker.com/maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /app

COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn -B clean package -DskipTests -pl logging-rest -am

FROM internal.example.com/hub.docker.com/eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=0 /app/logging-rest/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]