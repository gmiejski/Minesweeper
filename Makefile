.Phony: build run

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
