package com.gmiejski.minesweeper.game.domain

import org.springframework.stereotype.Service
import java.util.*

typealias GameID = String

class GameNotFound(val gameID: GameID) : RuntimeException("Game $gameID not found")
class AlreadyDiscovered(val gameID: GameID, val fieldCoordinate: FieldCoordinate) : RuntimeException("Field $fieldCoordinate already discovered on game $gameID")
class AlreadyStarted(val gameID: GameID) : RuntimeException("Game $gameID already started.")
class CannotToggleField(val gameID: GameID, val field: FieldCoordinate, reason: String) : RuntimeException("Cannot toggle field $field at game $gameID. Reason: $reason")
class CannotDiscoverToggledField(val gameID: GameID, val field: FieldCoordinate) : RuntimeException("Field $field on game $gameID cannot be discovered because it's flagged!")
class GameAlreadyEnded(val gameID: GameID, val status: GameStatus) : RuntimeException("Game $gameID already ended with status: $status")

@Service
class MineSweeperGameService(private val repository: GameRepository, private val commandHandler: GameCommandHandler) {

    fun startGame(rows: Int, columns: Int, bombsCount: Int): Game {
        val gameID = UUID.randomUUID().toString()
        val events = commandHandler.process(Game(gameID), CreateGameCommand(rows, columns, bombsCount))

        val game = repository.applyAll(Game(gameID), events)
        return game
    }

    fun executeAction(gameID: GameID, command: Command) {
        val game = repository.find(gameID) ?: throw GameNotFound(gameID)
        val events = this.commandHandler.process(game, command)
        this.repository.applyAll(game, events)
    }

    fun getGameView(gameID: GameID): GameView {
        val game = repository.find(gameID)
        return game?.getView() ?: throw GameNotFound(gameID)
    }
}