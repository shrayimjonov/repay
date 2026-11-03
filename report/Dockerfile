# Stage 1: Build stage
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
#RUN gradle build --no-daemon
RUN gradle build -x test

# Stage 2: Production stage
FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
