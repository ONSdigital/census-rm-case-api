FROM eclipse-temurin:21-jre-alpine

CMD ["/opt/java/openjdk/bin/java", "-jar", "/opt/census-rm-caseapi.jar"]

# Create a system group and user without forcing UID/GID
RUN addgroup --system caseapi && \
    adduser --system --ingroup caseapi caseapi

USER caseapi

COPY target/census-rm-case-api*.jar /opt/census-rm-case-api.jar


