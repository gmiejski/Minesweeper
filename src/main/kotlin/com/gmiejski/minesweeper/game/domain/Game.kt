package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.game.domain.grid.*
import java.time.LocalDateTime
import kotlin.random.Random

enum class DiscoveryResult { // TODO remove
    BOMB, EMPTY
}

enum class GameStatus {
    NOT_INITIALIZED, IN_PROGRESS, LOST, WON
}

data class FieldCoordinate(val row: Int, val column: Int)

data class GameCreatedEvent(override val target: GameID, val rows: Int, val columns: Int, val bombsCoordinates: Set<FieldCoordinate>) : DomainEvent(target)
data class GameLost(override val target: GameID) : DomainEvent(target)
data class GameWon(override val target: GameID) : DomainEvent(target)
data class BombExploded(override val target: GameID, val bombField: FieldCoordinate) : DomainEvent(target)
data class FieldDiscoveredEvent(override val target: GameID, val discoveredField: FieldCoordinate, val allDiscoveredFields: List<FieldCoordinate>) : DomainEvent(target)
data class FieldToggled(override val target: GameID, val coordinate: FieldCoordinate) : DomainEvent(target)

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
        val gameCreatedEvent = GameCreatedEvent(Random.nextInt(), rows, cols, this.bombsCoordinates).occurredAt(LocalDateTime.now())
        return EventHandler().applyAll(Game(Random.nextInt()), listOf(gameCreatedEvent))// TODO Event handler should be hidden?
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