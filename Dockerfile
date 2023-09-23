FROM gradle:5.6.3-jdk11@sha256:3b18663bb8a3ea2f1ea61cbff00ba0efed6ecffaceccf7f161718046ce84f09b as builder

COPY --chown=gradle:gradle . .

RUN gradle --no-daemon clean build -x test

FROM openjdk:11-jdk-slim@sha256:29ce7049474178e55cf023b8e1f4cb6469250a67040fbb98656f2303efa5aded

RUN addgroup --system app && adduser --system --ingroup app app
USER app

WORKDIR /app

COPY --chown=app:app --from=builder /home/gradle/build/libs/jumysbar-*.jar /app/

EXPOSE 8080

ENV JVM_OPTS=""
ENTRYPOINT exec java ${JVM_OPTS} -jar /app/jumysbar-*.jar --spring.application.json="$SPRING_BOOT_PROPERTIES"