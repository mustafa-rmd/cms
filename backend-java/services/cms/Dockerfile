# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy the entire backend-java directory to handle multi-module structure
COPY . .

# Build the specific service
RUN mvn clean package -pl services/cms -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/services/cms/target/*.jar app.jar

ENV JAVA_OPTS=""
ENV OTHER_JAVA_OPTS=""

ARG VERSION="0.0.1-SNAPSHOT"
ARG BUILD_TIMESTAMP="unknown"
ARG GIT_HASH="unknown"
ARG GIT_BRANCH="main"
ARG GIT_COMMIT_TIMESTAMP="unknown"

ENV VERSION $VERSION
ENV BUILD_TIMESTAMP $BUILD_TIMESTAMP
ENV GIT_HASH $GIT_HASH
ENV GIT_BRANCH $GIT_BRANCH
ENV GIT_COMMIT_TIMESTAMP $GIT_COMMIT_TIMESTAMP

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $OTHER_JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"] 