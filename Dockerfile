# MAVEN_BUILD
FROM maven:3.8.7-openjdk-18 AS MAVEN_BUILD
LABEL maintainer="Lvoxx" email="lvoxxartist@gmail.com"
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build
RUN mvn package

# LAYERS_BUILD
FROM eclipse-temurin:21-jdk-alpine-3.21 as LAYERS_BUILD
WORKDIR /application
ARG JAR_FILE=/build/target/*.jar
COPY --from=MAVEN_BUILD ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# BUILD IMAGE

FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /application
EXPOSE 9090
COPY --from=LAYERS_BUILD application/dependencies/ ./
COPY --from=LAYERS_BUILD application/spring-boot-loader ./
COPY --from=LAYERS_BUILD application/snapshot-dependencies/ ./
COPY --from=LAYERS_BUILD application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
