FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /build
COPY . .
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

<<<<<<< HEAD
COPY --from=build /build/target/*.jar app.jar
=======
COPY target/glamgest-0.0.1-SNAPSHOT.jar app.jar
>>>>>>> 51408e4fe9d7e969b11538bbc8c522b9d34e13aa

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
