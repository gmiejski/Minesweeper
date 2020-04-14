package com.gmiejski.minesweeper.game.domain

import java.time.LocalDateTime
import java.util.stream.IntStream
import kotlin.random.Random
import kotlin.streams.toList


interface Command

data class DiscoverFieldCommand(val fieldCoordinate: FieldCoordinate) : Command
class CreateGameCommand : Command

class GameCommandHandler(val generator: Generator) {
    fun process(game: Game, command: Command): List<GameEvent> {
        return when (command) {
            is CreateGameCommand -> this.newGame(game)
            is DiscoverFieldCommand -> this.tryToDiscover(game, command.fieldCoordinate)
            else -> listOf()
        }
    }

    private fun tryToDiscover(game: Game, fieldCoordinate: FieldCoordinate): List<GameEvent> {
        return when (game.canDiscover(fieldCoordinate)) {
            DiscoverTry.ALREADY_DISCOVERED -> {
                throw AlreadyDiscovered(fieldCoordinate)
            }
            DiscoverTry.CAN_BE_DISCOVERED -> {
                listOf(FieldDiscoveredEvent(game.gameID, fieldCoordinate, LocalDateTime.now()))
            }
        }
    }

    private fun newGame(game: Game): List<GameEvent> {
        if (game.status() == GameStatus.NOT_INITIALIZED) {
            throw AlreadyStarted(game.gameID)
        }
        return listOf(GameCreatedEvent(game.gameID, 10, 10, generator.generate(10, 10, 15), LocalDateTime.now()))
    }
}

class Generator {
    fun generate(rows: Int, columns: Int, bombsCount: Int): Map<FieldCoordinate, Field> {
        val all = IntStream.range(1, rows + 1).mapToObj { h ->
            IntStream.range(1, columns + 1).mapToObj { w ->
                val coordinate = FieldCoordinate(h, w)
                Field(coordinate, false)
            }.toList()
        }.toList()

        IntRange(0, bombsCount).forEach {
            var x: Int
            var y: Int
            do {
                x = Random.nextInt(1, rows)
                y = Random.nextInt(1, columns)

            } while (!all.get(x).get(y).isBomb)
            all.get(x).get(y).isBomb = true
        }
        return all.flatten().groupBy { it.position }.mapValues { it.value.first() }
    }
}

class AlreadyDiscovered(fieldCoordinate: FieldCoordinate) : RuntimeException("Field $fieldCoordinate already discovered")
class AlreadyStarted(gameID: GameID) : RuntimeException("Game $gameID already started.")