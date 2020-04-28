.Phony: build run


test:
	SPRING_PROFILES_ACTIVE=local ./gradlew clean test --info

build:
	./gradlew bootJar
	docker build -t mine-sweeper:local -f Dockerfile .


local-development:
	docker-compose run -d -p27017:27017 mongo

deploy-local:
	./gradlew bootJar
	docker build -t mine-sweeper:1.0.0 -f Dockerfile .
	kubectl apply -f k8s-configuration/deployment.yaml

restart-local-k8s:
	kubectl rollout restart deployment minesweeper-api

mongo-deploy-local-k8s:
	helm upgrade -f k8s-configuration/mongodb.yaml  minesweeper-mongo-40 stable/mongodb-replicaset
