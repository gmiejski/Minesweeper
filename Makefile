
test-game:
	cd minesweeper-game ; make -f Makefile test

run-minesweeper-game:
	cd minesweeper-game ; make build-game
	docker-compose run -p8080:8080 minesweeper-game

run-minesweeper:
	cd minesweeper-game ; make build-service
	cd minesweeper-otherservice ; make build-service
	docker-compose up

test:
	SPRING_PROFILES_ACTIVE=local ./gradlew clean test --info

deploy-local:
	cd minesweeper-game ; make deploy-local
	cd minesweeper-otherservice ; make deploy-local
