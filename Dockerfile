ARG BUILDPLATFORM TARGETPLATFORM

FROM --platform=$BUILDPLATFORM maven:3 AS jdk
WORKDIR /src
COPY . .
ENV SPRING_AI_VECTORSTORE_REDIS_INITIALIZE_SCHEMA=false
RUN --mount=type=cache,target=/root/.m2 mvn verify

FROM ghcr.io/graalvm/native-image-community:21 AS native-builder
WORKDIR /src
COPY . .
ENV SPRING_AI_VECTORSTORE_REDIS_INITIALIZE_SCHEMA=false
RUN --mount=type=cache,target=/root/.m2 ./mvnw -Pnative native:compile

FROM ubuntu AS native-runner
COPY --from=native-builder /src/target/cookbook .
CMD ["./cookbook"]
EXPOSE 8080

FROM eclipse-temurin:21-jre-alpine AS jre
WORKDIR /app
COPY --from=jdk /src/target .
CMD exec java -jar cookbook-0.0.1-SNAPSHOT.jar
EXPOSE 8080
