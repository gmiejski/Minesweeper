package com.gmiejski.minesweeper.game.domain


class GameRepository(val eventHandler: EventHandler, val eventStore: EventStore) {
    fun find(gameID: GameID): Game? {
        val events = eventStore.get(gameID)
        if (events.isEmpty()) {
            return null
        }
        return eventHandler.applyAll(Game(gameID), events)
    }

    fun applyAll(game: Game, events: List<GameEvent>): Game {
        return this.eventHandler.applyAll(game, events)
    }
}

interface EventStore {
    fun get(gameID: GameID): List<GameEvent>
    fun saveAll(gameID: GameID, events: List<GameEvent>)
}

class InMemoryEventStore : EventStore {
    val events: MutableList<GameEvent> = mutableListOf()
    override fun get(gameID: GameID): List<GameEvent> {
        return events.filter { it.target == gameID }.sortedBy { it.time }
    }

    override fun saveAll(gameID: GameID, events: List<GameEvent>) {
        this.events.addAll(events)
    }
}