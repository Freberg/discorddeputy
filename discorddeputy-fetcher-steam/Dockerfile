FROM eclipse-temurin:25-jre-jammy
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","--enable-native-access=ALL-UNNAMED","-Dspring.aot.enabled=true","-jar","/app.jar"]