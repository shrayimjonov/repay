FROM internal.example.com/hub.docker.com/maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /app
COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn clean install -Djasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD} # -DskipTests

FROM internal.example.com/hub.docker.com/eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=0 /app/target/*.jar ./app.jar
