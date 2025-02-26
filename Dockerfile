ARG MAVEN_VERSION=3.9.9
ARG JAVA_VERSION=21
ARG PORT=9090

# ------------ MAVEN_BUILD ------------
FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JAVA_VERSION} AS BUILD_LAYER
LABEL maintainer="Lvoxx" email="lvoxxartist@gmail.com"

# Copy Maven project files
COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build
RUN mvn package -DskipTests

# ------------ LAYERS_BUILD ------------
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine-3.21 as LAYERS_BUILD
WORKDIR /application
ARG JAR_FILE=/build/target/*.jar

# Copy and extract JAR layers
COPY --from=BUILD_LAYER ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# ------------ BUILD IMAGE ------------
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine-3.21

# Copy extracted layers
WORKDIR /application
COPY --from=LAYERS_BUILD application/dependencies/ ./
COPY --from=LAYERS_BUILD application/spring-boot-loader/ ./
COPY --from=LAYERS_BUILD application/snapshot-dependencies/ ./
COPY --from=LAYERS_BUILD application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
