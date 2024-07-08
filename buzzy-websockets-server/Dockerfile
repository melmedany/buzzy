FROM azul/zulu-openjdk:21-jre-latest
COPY build/libs/buzzy-websockets-server-*.jar  app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]