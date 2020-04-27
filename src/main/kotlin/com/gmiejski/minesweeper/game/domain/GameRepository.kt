package com.gmiejski.minesweeper.game.domain

import org.springframework.stereotype.Repository

@Repository
class GameRepository(val eventHandler: EventHandler, val eventStore: EventStore) {
    fun find(gameID: GameID): Game? {
        val events = eventStore.get(gameID)
        if (events.isEmpty()) {
            return null
        }
        return eventHandler.applyAll(Game(gameID), events)
    }

    fun applyAll(game: Game, events: List<DomainEvent>): Game {
        val applyAll = this.eventHandler.applyAll(game, events)
        this.eventStore.saveAll(game.gameID, events)
        return applyAll
    }
}