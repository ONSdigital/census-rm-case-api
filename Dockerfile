FROM mirror.gcr.io/library/eclipse-temurin:21-jre-alpine

ARG JAR_FILE=census-rm-case-api*.jar
CMD ["/opt/java/openjdk/bin/java", "-jar", "/opt/census-rm-case-api.jar"]

# Create a system group and user without forcing UID/GID
RUN addgroup --system case-api && \
    adduser --system --ingroup case-api case-api

USER case-api

COPY target/$JAR_FILE /opt/census-rm-case-api.jar

