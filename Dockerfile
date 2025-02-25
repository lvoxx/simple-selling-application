# ------------ MAVEN_BUILD ------------
FROM maven:3.8.7-openjdk-18 AS BUILD
LABEL maintainer="Lvoxx" email="lvoxxartist@gmail.com"
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build
RUN mvn package

# ------------ LAYERS_BUILD ------------
FROM eclipse-temurin:21-jdk-alpine-3.21 as LAYERS_BUILD
WORKDIR /app
ARG JAR_FILE=/build/target/*.jar
COPY --from=BUILD ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ------------ BUILD IMAGE ------------
FROM eclipse-temurin:21-jre-alpine-3.21

# Install bash and netcat (nc) for wait-for-it.sh
RUN apk add --no-cache bash netcat-openbsd

WORKDIR /app

# Copy wait-for-it.sh script
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

COPY --from=LAYERS_BUILD app/dependencies/ ./
COPY --from=LAYERS_BUILD app/spring-boot-loader ./
COPY --from=LAYERS_BUILD app/snapshot-dependencies/ ./
COPY --from=LAYERS_BUILD app/app/ ./
EXPOSE 9090
ENTRYPOINT ["/app/wait-for-it.sh", "postgres:5432","--", "echo", "Postgres is up", "--", "java", "org.springframework.boot.loader.JarLauncher"]
