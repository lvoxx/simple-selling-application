ARG PORT=9090

# ------------ UBUNTU_BUILD ------------
FROM ubuntu:24.04 AS ubuntu_layer

# Install FFmpeg
RUN apt-get update && apt-get install -y --no-install-recommends ffmpeg && \
    rm -rf /var/lib/apt/lists/*

# ------------ MAVEN_BUILD ------------
FROM maven:3.9.9-eclipse-temurin-21 AS build_layer
LABEL maintainer="Lvoxx" email="lvoxxartist@gmail.com"

# Copy Maven project files
COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build
RUN mvn package -DskipTests

# ------------ layers_build ------------
FROM eclipse-temurin:21-jre-alpine-3.21 AS layers_build
WORKDIR /application
ARG JAR_FILE=/build/target/*.jar

# Copy and extract JAR layers
COPY --from=build_layer ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# ------------ BUILD IMAGE ------------
FROM gcr.io/distroless/java21-debian12

# Copy extracted layers
WORKDIR /deploy
COPY --from=layers_build application/dependencies/ ./
COPY --from=layers_build application/spring-boot-loader/ ./
COPY --from=layers_build application/snapshot-dependencies/ ./
COPY --from=layers_build application/application/ ./
# Copy the FFmpeg binary and necessary libraries from the build stage
COPY --from=ubuntu_layer /usr/bin/ffmpeg /usr/bin/ffmpeg
COPY --from=ubuntu_layer /usr/bin/ffprobe /usr/bin/ffprobe
COPY --from=ubuntu_layer /lib /lib
COPY --from=ubuntu_layer /lib64 /lib64
COPY --from=ubuntu_layer /usr/lib /usr/lib

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
