FROM provectuslabs/kafka-ui:latest

USER root

COPY certs/ca.crt /tmp/ca.crt

RUN keytool -delete -alias my-ca -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit || true && \
    keytool -import -noprompt \
    -alias my-ca \
    -file /tmp/ca.crt \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit

USER kafkaui