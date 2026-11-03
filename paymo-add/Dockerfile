FROM maven:3.9.3-eclipse-temurin-8-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
COPY --from=build /app/target/*.war ./app.war