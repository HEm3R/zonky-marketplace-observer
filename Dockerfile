FROM openjdk:8-alpine

LABEL maintainer="vac.chalupa@gmail.com"

VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=build/libs/zonky-marketplace-observer-1.0.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
