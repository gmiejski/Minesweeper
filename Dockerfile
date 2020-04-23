FROM openjdk:slim

RUN mkdir -p /opt/MineSweeper/lib/
COPY build/libs/MineSweeper-Demo-0.0.1-SNAPSHOT.jar /opt/MineSweeper/lib/

# ENTRYPOINT ["/usr/bin/java"]
ENV PORT 8080
EXPOSE 8080/tcp
CMD ["java", "-jar", "/opt/MineSweeper/lib/MineSweeper-Demo-0.0.1-SNAPSHOT.jar"]
