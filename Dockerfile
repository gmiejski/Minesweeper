FROM openjdk:slim

WORKDIR /opt/MineSweeper/lib/
RUN mkdir -p /opt/MineSweeper/lib/
COPY build/libs/*.jar /opt/MineSweeper/lib/mine-sweeper.jar

ENV PORT 8080
EXPOSE 8080/tcp
RUN pwd
RUN ls
CMD ["java", "-jar", "/opt/MineSweeper/lib/mine-sweeper.jar"]
