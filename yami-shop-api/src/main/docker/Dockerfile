#FROM openjdk:8-jdk
FROM docker.io/library/openjdk:17.0.2
VOLUME /tmp
RUN mkdir -p /opt/projects/mall4j
WORKDIR /opt/projects/mall4j
EXPOSE 8086
ADD yami-shop-api-0.0.1-SNAPSHOT.jar shop-admin.jar
ENTRYPOINT ["java", "-Xms200m", "-Xmx1G", "-Xmn256m", "-Xss256k", "-XX:SurvivorRatio=8", "-XX:+UseConcMarkSweepGC", "-Duser.timezone=GMT+08", "-jar", "/shop-admin.jar"]
