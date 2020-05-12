FROM openjdk:8-alpine

COPY target/uberjar/clj-oss.jar /clj-oss/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clj-oss/app.jar"]
