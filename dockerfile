FROM openjdk:17

ARG JAR_FILE=*.jar
ARG JAR_FILE=/build/libs/waka-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]
