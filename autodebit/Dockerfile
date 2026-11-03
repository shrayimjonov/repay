FROM maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /app
COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn clean install -DskipTests
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=0 /app/target/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]