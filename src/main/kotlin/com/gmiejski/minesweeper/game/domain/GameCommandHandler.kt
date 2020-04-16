package com.gmiejski.minesweeper.game.domain


interface Command

data class DiscoverFieldCommand(val fieldCoordinate: FieldCoordinate) : Command
class CreateGameCommand : Command

class GameCommandHandler(val generator: RandomGenerator) {
    fun process(game: Game, command: Command): List<DomainEvent> {
        return when (command) {
            is CreateGameCommand -> this.newGame(game)
            is DiscoverFieldCommand -> this.tryToDiscover(game, command.fieldCoordinate)
            else -> listOf()
        }
    }

    private fun tryToDiscover(game: Game, fieldCoordinate: FieldCoordinate): List<DomainEvent> {
        return when (game.canDiscover(fieldCoordinate)) {
            DiscoverTry.ALREADY_DISCOVERED -> {
                throw AlreadyDiscovered(fieldCoordinate)
            }
            DiscoverTry.CAN_BE_DISCOVERED -> {
                listOf(FieldDiscoveredEvent(game.gameID, fieldCoordinate, listOf(fieldCoordinate)))
            }
        }
    }

    private fun newGame(game: Game): List<DomainEvent> {
        if (game.status() == GameStatus.NOT_INITIALIZED) {
            throw AlreadyStarted(game.gameID)
        }
        return listOf(GameCreatedEvent(game.gameID, 10, 10, generator.generate(10, 10, 15)))
    }
}


