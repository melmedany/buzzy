FROM azul/zulu-openjdk:21-jre-latest
COPY build/libs/buzzy-sso-*.jar  app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]