package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.game.domain.grid.BoardView
import kotlin.random.Random

typealias GameID = Int

class GameNotFound(gameID: GameID) : RuntimeException("Game $gameID not found")

class MineSweeperGameService(val repository: GameRepository, val commandHandler: GameCommandHandler) {

    fun startGame(): Game {
        val gameID = Random.nextInt()
        val events = commandHandler.process(Game(gameID), CreateGameCommand())

        val game = repository.applyAll(Game(gameID), events)
        return game
    }

    fun executeAction(gameID: GameID, command: Command) {
        val find = repository.find(gameID) ?: throw GameNotFound(gameID)

    }

    fun getGameGrid(gameID: GameID): BoardView {
        return BoardView(1, 1, emptyList()) // TODO
    }
}