package com.gmiejski.minesweeper.game.domain

interface EventStore {
    fun get(gameID: GameID): List<DomainEvent>
    fun saveAll(gameID: GameID, events: List<DomainEvent>)
}

class InMemoryEventStore : EventStore {
    val events: MutableList<DomainEvent> = mutableListOf()
    override fun get(gameID: GameID): List<DomainEvent> {
        return events.filter { it.getID() == gameID }.sortedBy { it.time }
    }

    override fun saveAll(gameID: GameID, events: List<DomainEvent>) {
        this.events.addAll(events)
    }
}