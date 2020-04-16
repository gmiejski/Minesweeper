package com.gmiejski.minesweeper.game.domain

import java.time.LocalDateTime


abstract class DomainEvent(open val target: GameID) {
    var time: LocalDateTime

    init {
        time = LocalDateTime.now()
    }

    fun occurredAt(time: LocalDateTime): DomainEvent {
        return this
    }
}

fun Collection<DomainEvent>.withDate(time: LocalDateTime): List<DomainEvent> {
    return this.map { it.occurredAt(time) }
}

class EventHandler {
    fun applyAll(game: Game, events: List<DomainEvent>): Game {
        events.forEach {
            events.forEach { event ->
                when (event) {
                    is GameCreatedEvent -> {
                        game.initializeGame(event.rows, event.columns, event.bombsCoordinates)
                    }
                    is FieldDiscoveredEvent -> {
                        game.confirmDiscovery(event.allDiscoveredFields)
                    }
                }
            }
        }
        return game
    }
}