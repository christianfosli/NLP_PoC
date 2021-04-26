FROM adoptopenjdk:16-jdk AS build
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package

FROM adoptopenjdk:16-jre
WORKDIR /app
COPY --from=build /app/target/api-*-SNAPSHOT.jar api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "api.jar"]
