FROM openjdk:11-slim
CMD ["/usr/local/openjdk-11/bin/java", "-jar", "/opt/census-rm-case-api.jar"]

RUN groupadd --gid 999 caseapi && \
    useradd --create-home --system --uid 999 --gid caseapi caseapi

RUN apt-get update && \
apt-get -yq install curl && \
apt-get -yq clean && \
rm -rf /var/lib/apt/lists/*

USER caseapi

ARG JAR_FILE=census-rm-case-api*.jar
COPY target/$JAR_FILE /opt/census-rm-case-api.jar
