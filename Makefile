.Phony: build run

build-game:
	./gradlew bootJar
	docker build -t mine-sweeper:v1 -f Dockerfile .

run:
	docker-compose run -p 8080:8080  mine-sweeper
