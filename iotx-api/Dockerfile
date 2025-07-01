# TODO，没有实际填写
#FROM ubuntu:latest
#LABEL authors="10107"
#
#ENTRYPOINT ["top", "-b"]


FROM openjdk:8-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]