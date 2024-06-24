FROM azul/zulu-openjdk:21-jre-latest
COPY build/libs/buzzy-conversation-api-*.jar  app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]