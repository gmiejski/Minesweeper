.Phony: build run


test:
	./gradlew clean test

build-game:
	./gradlew bootJar
	docker build -t mine-sweeper:v1 -f Dockerfile .

run:
	docker-compose run -p 8080:8080  mine-sweeper

deploy-local:
	kubectl apply -f k8s-configuration/minesweeper.deployment.yaml

restart-local-k8s:
	kubectl rollout restart deployment minesweeper-api

mongo-deploy-local-k8s:
	helm upgrade -f k8s-configuration/mongodb.yaml  minesweeper-mongo-40 stable/mongodb-replicaset

compose-local-development:
	docker-compose run -d -p27017:27017 mongo
