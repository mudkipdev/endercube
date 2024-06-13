FROM azul/zulu-openjdk-alpine:21-jre-headless
COPY /build/libs/endercube-?.?.?.jar app.jar
EXPOSE 25565/tcp
CMD ["java", "-jar", "/app.jar"]