FROM hub.c.163.com/library/java:latest
VOLUME /tmp
ADD target/manager-portal-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]