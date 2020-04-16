package com.gmiejski.minesweeper.game.domain


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

interface EventStore {
    fun get(gameID: GameID): List<DomainEvent>
    fun saveAll(gameID: GameID, events: List<DomainEvent>)
}

class InMemoryEventStore : EventStore {
    val events: MutableList<DomainEvent> = mutableListOf()
    override fun get(gameID: GameID): List<DomainEvent> {
        return events.filter { it.target == gameID }.sortedBy { it.time }
    }

    override fun saveAll(gameID: GameID, events: List<DomainEvent>) {
        this.events.addAll(events)
    }
}