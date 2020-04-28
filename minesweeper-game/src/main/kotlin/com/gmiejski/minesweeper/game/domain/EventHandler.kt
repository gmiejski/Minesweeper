package com.gmiejski.minesweeper.game.domain

import org.springframework.stereotype.Component
import java.time.LocalDateTime



fun Collection<DomainEvent>.withDate(time: LocalDateTime): List<DomainEvent> {
    return this.map { it.occurredAt(time) }
}

@Component
class EventHandler {
    fun applyAll(game: Game, events: List<DomainEvent>): Game {
        events.forEach { event ->
            when (event) {
                is GameCreatedEvent -> {
                    game.initializeGame(event.rows, event.columns, event.bombsCoordinates)
                }
                is FieldDiscoveredEvent -> {
                    game.confirmDiscovery(event.allDiscoveredFields)
                }
                is FieldToggled -> {
                    game.toggle(event.coordinate)
                }
                is GameLost -> {
                    game.changeStatus(GameStatus.LOST)
                }
                is GameWon -> {
                    game.changeStatus(GameStatus.WON)
                }
            }
        }
        return game
    }
}