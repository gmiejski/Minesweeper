package com.gmiejski.minesweeper.game.domain

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer
import com.gmiejski.minesweeper.game.domain.grid.*
import java.time.LocalDateTime
import java.util.*

enum class DiscoveryResult { // TODO remove
    BOMB, EMPTY
}

enum class GameStatus {
    NOT_INITIALIZED, IN_PROGRESS, LOST, WON
}

data class FieldCoordinate(
    @field:TaggedFieldSerializer.Tag(0) val row: Int,
    @field:TaggedFieldSerializer.Tag(1) val column: Int) {

    constructor() : this(0, 0)
}

sealed class DomainEvent() {
    var time: LocalDateTime

    init {
        time = LocalDateTime.now()
    }

    fun occurredAt(time: LocalDateTime): DomainEvent {
        return this
    }

    abstract fun getID(): GameID // TODO AggregateID rather than GameID
}


data class GameCreatedEvent(
    @field:TaggedFieldSerializer.Tag(0) val target: GameID,
    @field:TaggedFieldSerializer.Tag(1) val rows: Int,
    @field:TaggedFieldSerializer.Tag(2) val columns: Int,
    @field:TaggedFieldSerializer.Tag(3) val bombsCoordinates: Set<FieldCoordinate>
) : DomainEvent() {
    constructor() : this("", 0, 0, setOf())

    override fun getID(): GameID {
        return target
    }
}

data class GameLost(val target: GameID) : DomainEvent() {
    override fun getID(): GameID {
        return target

    }
}

data class GameWon(val target: GameID) : DomainEvent() {
    override fun getID(): GameID {
        return target

    }
}

data class BombExploded(val target: GameID, val bombField: FieldCoordinate) : DomainEvent() {
    override fun getID(): GameID {
        return target

    }
}

data class FieldDiscoveredEvent(val target: GameID, val discoveredField: FieldCoordinate, val allDiscoveredFields: List<FieldCoordinate>) : DomainEvent() {
    override fun getID(): GameID {
        return target

    }
}

data class FieldToggled(val target: GameID, val coordinate: FieldCoordinate) : DomainEvent() {
    override fun getID(): GameID {
        return target

    }
}

class Game(val gameID: GameID) {
    private var currentStatus: GameStatus = GameStatus.IN_PROGRESS
    private lateinit var gameGrid: GameGrid // TODO change for non-lateinit empty Grid

    fun discover(fieldCoordinate: FieldCoordinate): List<DomainEvent> {
        if (this.gameEnded()) throw GameAlreadyEnded(gameID, currentStatus)
        if (gameGrid.isDiscovered(fieldCoordinate)) throw AlreadyDiscovered(gameID, fieldCoordinate)
        if (gameGrid.isToggled(fieldCoordinate)) throw CannotDiscoverToggledField(gameID, fieldCoordinate)
        if (gameGrid.isBomb(fieldCoordinate)) {
            val now = LocalDateTime.now()
            return listOf(BombExploded(gameID, fieldCoordinate).occurredAt(now), GameLost(gameID).occurredAt(now))
        }
        val allCoordinates = gameGrid.discoverAllAround(fieldCoordinate)
        this.confirmDiscovery(allCoordinates)
        return appendWonGame(listOf(FieldDiscoveredEvent(gameID, fieldCoordinate, allCoordinates)))
    }

    private fun gameEnded(): Boolean {
        return this.currentStatus in listOf(GameStatus.WON, GameStatus.LOST)
    }

    private fun appendWonGame(events: List<DomainEvent>): List<DomainEvent> {
        if (this.isWon()) {
            return events.plus(GameWon(gameID))
        }
        return events
    }

    private fun isWon(): Boolean {
        return this.gameGrid.allFieldsDiscovered()
    }

    fun status(): GameStatus {
        return this.currentStatus
    }

    fun getView(): GameView {
        return GameView(this.currentStatus, gameGrid.getView())
    }

    fun initializeGame(rows: Int, columns: Int, bombsCoordinates: Set<FieldCoordinate>) {
        this.currentStatus = GameStatus.IN_PROGRESS
        this.gameGrid = GameGrid(rows, columns, bombsCoordinates)
    }

    fun confirmDiscovery(allDiscoveredFields: List<FieldCoordinate>) {
        allDiscoveredFields.forEach { this.gameGrid.mapAsDiscovered(it) }
    }

    fun toggle(coordinate: FieldCoordinate): List<DomainEvent> {
        if (this.gameEnded()) throw GameAlreadyEnded(gameID, currentStatus)
        if (this.gameGrid.isDiscovered(coordinate)) throw CannotToggleField(this.gameID, coordinate, "Cannot toggle discovered field")

        this.gameGrid.toggle(coordinate)
        return listOf(FieldToggled(gameID, coordinate))
    }

    fun changeStatus(target: GameStatus) {
        if (this.gameEnded()) throw RuntimeException("Game: ${gameID}: cannot change game status after it ended!")
        this.currentStatus = target
    }
}

class GameBuilder(val rows: Int, val cols: Int) {
    private lateinit var bombsCoordinates: Set<FieldCoordinate>

    fun bombs(bombsPositions: Set<FieldCoordinate>): GameBuilder {
        this.bombsCoordinates = bombsPositions
        return this
    }

    fun build(): Game {
        val gameID = UUID.randomUUID().toString()
        val gameCreatedEvent = GameCreatedEvent(gameID, rows, cols, this.bombsCoordinates).occurredAt(LocalDateTime.now())
        return EventHandler().applyAll(Game(gameID), listOf(gameCreatedEvent))// TODO Event handler should be hidden?
    }
}

class GameView(val status: GameStatus, val view: BoardView) {
    fun toDTO(): GameViewDTO {
        val gridDTO = BoardViewGridDTO(view.gridRows.map { BoardViewRowDTO(it.fields.map { BoardViewFieldDTO(it.coordinate, it.state) }) })
        return GameViewDTO(status, view.rows, view.columns, gridDTO)
    }

    fun getFieldValue(coordinate: FieldCoordinate): BoardViewField {
        return view.getFieldValue(coordinate)
    }
}