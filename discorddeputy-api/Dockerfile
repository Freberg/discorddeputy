FROM eclipse-temurin:25-jre-jammy
COPY build/libs/* app.jar
ENTRYPOINT ["java","--enable-native-access=ALL-UNNAMED","-Dspring.aot.enabled=true","-jar","/app.jar"]