FROM maven:3
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn verify

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=0 /src/target .
CMD exec java -jar cookbook-0.0.1-SNAPSHOT.jar
EXPOSE 8080
