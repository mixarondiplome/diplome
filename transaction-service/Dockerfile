FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY certs/ /app/certs/

COPY transaction-service/target/*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]