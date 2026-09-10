FROM eclipse-temurin:17-jre-jammy

ARG APP_UID=1000
ARG APP_GID=1000

RUN groupadd -r -g ${APP_GID} appuser \
    && useradd -r -u ${APP_UID} -g appuser appuser

WORKDIR /app

COPY lib/ /app/lib/
COPY lib/java-application-template*.jar /app/lib/app.jar
COPY config/application.yml /app/config/application.yml
COPY config/application-default.yml /app/config/application-default.yml
COPY docker/entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh \
    && mkdir -p /app/logs /app/data \
    && chown -R appuser:appuser /app

USER appuser
EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
