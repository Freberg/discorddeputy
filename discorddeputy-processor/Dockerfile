FROM eclipse-temurin:27-jre-jammy
COPY build/libs/* app.jar
ENTRYPOINT ["java","--enable-native-access=ALL-UNNAMED","-Dspring.aot.enabled=true","-jar","/app.jar"]
