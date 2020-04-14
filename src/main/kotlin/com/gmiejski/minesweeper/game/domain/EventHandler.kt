package com.gmiejski.minesweeper.game.domain

import java.time.LocalDateTime


interface GameEvent {
    val time: LocalDateTime // TODO Replace with versioning?
    val target: GameID
}

data class GameCreatedEvent(override val target: GameID, val width: Int, val height: Int, val fields: Map<FieldCoordinate, Field>, override val time: LocalDateTime) : GameEvent
data class FieldDiscoveredEvent(override val target: GameID, val field: FieldCoordinate, override val time: LocalDateTime) : GameEvent


class EventHandler {
    fun applyAll(game: Game, events: List<GameEvent>): Game {
        events.forEach {
            events.forEach { event ->
                when (event) {
                    is GameCreatedEvent -> {
                        game.initializeGame(event.fields)
                    }
                }
            }
        }
        return game
    }
}