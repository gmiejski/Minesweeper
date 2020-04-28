
test-game:
	cd minesweeper-game ; make -f Makefile test

run-minesweeper-game:
	cd minesweeper-game ; make build-game
	docker-compose run -p8080:8080 minesweeper-game

run-minesweeper:
	cd minesweeper-game ; make build
	cd minesweeper-otherservice ; make build
	docker-compose up -p8080:8080

test:
	SPRING_PROFILES_ACTIVE=local ./gradlew clean test
