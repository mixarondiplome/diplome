FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN adduser -D appuser

COPY certs/ /app/certs/

COPY --from=build /app/target/*.jar app.jar

USER appuser

ENV JAVA_OPTS=""

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]