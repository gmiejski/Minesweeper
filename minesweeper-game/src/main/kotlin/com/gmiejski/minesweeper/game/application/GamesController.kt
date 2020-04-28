package com.gmiejski.minesweeper.game.application

import com.gmiejski.minesweeper.game.domain.*
import com.gmiejski.minesweeper.game.domain.grid.GameViewDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class GameCreatedDTO(val gameID: GameID)
data class DiscoverFieldRequest(val row: Int, val col: Int)


@RestController("/")
class GamesController(val gameService: MineSweeperGameService) {

    @PostMapping("/games")
    fun createGame(): ResponseEntity<GameCreatedDTO> {
        val game = gameService.startGame(10, 10, 5)
        return ResponseEntity.ok(GameCreatedDTO(game.gameID))
    }

    @GetMapping("/games/{id}")
    fun getGrid(@PathVariable("id") gameID: GameID): ResponseEntity<GameViewDTO> {
        try {
            val gameGrid = gameService.getGameView(gameID)
            return ResponseEntity.ok(gameGrid.toDTO())
        } catch (exception: GameNotFound) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/games/{id}/actions")
    fun discoverField(@PathVariable("id") gameID: GameID, @RequestBody request: DiscoverFieldRequest): ResponseEntity<Unit> {
        try {
            gameService.executeAction(gameID, DiscoverFieldCommand(FieldCoordinate(request.row, request.col)))
            return ResponseEntity.ok().build()
        } catch (exception: GameNotFound) {
            return ResponseEntity.notFound().build()
        }
    }
}