package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.game.domain.grid.BoardView
import kotlin.random.Random

typealias GameID = Int

class GameNotFound(val gameID: GameID) : RuntimeException("Game $gameID not found")
class AlreadyDiscovered(val gameID: GameID, val fieldCoordinate: FieldCoordinate) : RuntimeException("Field $fieldCoordinate already discovered on game $gameID")
class AlreadyStarted(val gameID: GameID) : RuntimeException("Game $gameID already started.")
class CannotToggleField(val gameID: GameID, val field: FieldCoordinate, reason: String) : RuntimeException("Cannot toggle field $field at game $gameID. Reason: $reason")
class CannotDiscoverToggledField(val gameID: GameID, val field: FieldCoordinate) : RuntimeException("Field $field on game $gameID cannot be discovered because it's flagged!")

class MineSweeperGameService(val repository: GameRepository, val commandHandler: GameCommandHandler) {

    fun startGame(rows: Int, columns: Int, bombsCount: Int): Game {
        val gameID = Random.nextInt()
        val events = commandHandler.process(Game(gameID), CreateGameCommand(rows, columns, bombsCount))

        val game = repository.applyAll(Game(gameID), events)
        return game
    }

    fun executeAction(gameID: GameID, command: Command) {
        val game = repository.find(gameID) ?: throw GameNotFound(gameID)
        val events = this.commandHandler.process(game, command)
        this.repository.applyAll(game, events)
    }

    fun getGameGrid(gameID: GameID): BoardView {
        val find = repository.find(gameID)
        return find?.getBoardView() ?: throw GameNotFound(gameID)
    }
}