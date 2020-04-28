FROM openjdk:slim

WORKDIR /opt/MineSweeper/lib/
RUN mkdir -p /opt/MineSweeper/lib/
COPY build/libs/*.jar /opt/MineSweeper/lib/mine-sweeper.jar

ENV PORT 8080
EXPOSE 8080/tcp

CMD ["java", "-jar", "/opt/MineSweeper/lib/mine-sweeper.jar"]

