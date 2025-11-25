# java base image
FROM openjdk:21-jdk-slim

LABEL maintainer="amolkkatte1@gmail.com"

WORKDIR /app
# port to access app
EXPOSE 8088

ENV APP_ENV=default

# app jar file
ADD target/pathlabmaster-0.0.1-SNAPSHOT.jar mfm-0.0.1-SNAPSHOT.jar

# run app
CMD java -Xmx1024m -jar /app/mfm-0.0.1-SNAPSHOT.jar