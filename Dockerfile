# ------------ MAVEN_BUILD ------------
FROM maven:3.9.9-eclipse-temurin-21 AS BUILD
LABEL maintainer="Lvoxx" email="lvoxxartist@gmail.com"
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build
RUN mvn package -DskipTests

# ------------ LAYERS_BUILD ------------
FROM eclipse-temurin:21-jdk-alpine-3.21 as LAYERS_BUILD
WORKDIR /application
ARG JAR_FILE=/build/target/*.jar
COPY --from=BUILD ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# ------------ BUILD IMAGE ------------
FROM eclipse-temurin:21-jre-alpine-3.21

# Install bash and netcat (nc) for wait-for-it.sh
RUN apk add --no-cache bash netcat-openbsd

WORKDIR /application

# Copy wait-for-it.sh script
COPY wait-for-it.sh /application/wait-for-it.sh
RUN chmod +x /application/wait-for-it.sh

COPY --from=LAYERS_BUILD application/dependencies/ ./
COPY --from=LAYERS_BUILD application/spring-boot-loader ./
COPY --from=LAYERS_BUILD application/snapshot-dependencies/ ./
COPY --from=LAYERS_BUILD application/application/ ./
EXPOSE 9090
ENTRYPOINT ["/application/wait-for-it.sh", "postgres:5432","--", "echo", "Postgres is up", "--", "java", "org.springframework.boot.loader.JarLauncher"]
