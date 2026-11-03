FROM internal.example.com/hub.docker.com/maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /app
COPY lib/*.jar /app/lib/

#RUN mvn install:install-file -Dfile=lib/common-0.0.1-SNAPSHOT.jar -DgroupId=uz.repay -DartifactId=common -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
#RUN mvn install:install-file -Dfile=lib/repay-auto-configure-0.0.1-SNAPSHOT.jar -DgroupId=uz.nodir -DartifactId=repay-auto-configure -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar

COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn clean install -DskipTests

FROM internal.example.com/hub.docker.com/eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=0 /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

