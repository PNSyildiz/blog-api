FROM openjdk:17-alpine
COPY target/blog-0.0.1-SNAPSHOT-exec.jar blog-0.0.1-SNAPSHOT-exec.jar
ENTRYPOINT ["java","-jar","/blog-0.0.1-SNAPSHOT-exec.jar"]
EXPOSE 5000
