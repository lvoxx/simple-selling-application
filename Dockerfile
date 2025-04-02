ARG PORT=9090

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
# Copy statis FFmpeg
# Learn more: https://hub.docker.com/r/mwader/static-ffmpeg/
COPY --from=mwader/static-ffmpeg:7.1 /ffmpeg /usr/local/bin/
COPY --from=mwader/static-ffmpeg:7.1 /ffprobe /usr/local/bin/


ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]