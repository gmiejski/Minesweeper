package com.gmiejski.minesweeper.game.domain


interface Command

data class DiscoverFieldCommand(val fieldCoordinate: FieldCoordinate) : Command
class CreateGameCommand(val rows: Int, val columns: Int, val bombsCount: Int) : Command

class GameCommandHandler(val bombsCoordinatesGenerator: BombsCoordinatesGenerator) {
    fun process(game: Game, command: Command): List<DomainEvent> {
        return when (command) {
            is CreateGameCommand -> this.newGame(game, command)
            is DiscoverFieldCommand -> this.discover(game, command.fieldCoordinate)
            else -> listOf()
        }
    }

    private fun discover(game: Game, fieldCoordinate: FieldCoordinate): List<DomainEvent> {
        return game.discover(fieldCoordinate)
    }

    private fun newGame(game: Game, command: CreateGameCommand): List<DomainEvent> {
        if (game.status() == GameStatus.NOT_INITIALIZED) {
            throw AlreadyStarted(game.gameID)
        }
        return listOf(GameCreatedEvent(game.gameID, command.rows, command.columns, bombsCoordinatesGenerator.generate(command.rows, command.columns, command.bombsCount)))
    }
}
