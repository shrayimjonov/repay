FROM internal.example.com/hub.docker.com/maven:3.9.9-sapmachine-22
WORKDIR /app
COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn clean install -DskipTests

FROM internal.example.com/hub.docker.com/amazoncorretto:23-alpine3.20-full
WORKDIR /app
COPY --from=0 /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
