FROM gradle:6.3.0-jdk8

RUN mkdir -p /opt/MineSweeper/lib/
WORKDIR /opt/MineSweeper/lib/
COPY ./gradlew .
COPY build.gradle.kts .
COPY src ./src

ENTRYPOINT ["gradle", "--info" ,"--no-daemon"]
