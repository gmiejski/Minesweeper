.Phony: build run


test:
	./gradlew clean test

build-service:
	./gradlew bootJar
	docker build -t mine-sweeper-otherservice:local -f Dockerfile .

deploy-local:
	./gradlew bootJar
	docker build -t mine-sweeper-otherservice:1.0.0 -f Dockerfile .
	kubectl apply -f k8s-configuration/deployment.yaml
	kubectl apply -f k8s-configuration/service.yaml
