package com.gmiejski.minesweeper.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmiejski.minesweeper.game.application.DiscoverFieldRequest
import com.gmiejski.minesweeper.game.application.GameCreatedDTO
import com.gmiejski.minesweeper.game.domain.GameID
import com.gmiejski.minesweeper.game.domain.PredictableBombsCoordinatesGenerator
import com.gmiejski.minesweeper.game.domain.grid.GameViewDTO
import com.gmiejski.minesweeper.game.domain.grid.UNKNOWN
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders


class MineSweeperApplicationTests @Autowired constructor(val mapper: ObjectMapper) : AbstractIT() {

    @Autowired
    lateinit var generator: PredictableBombsCoordinatesGenerator

    @Test
    fun startAGame() {
        // given
        val createGameResult = mvc!!.perform(MockMvcRequestBuilders.post("/games")).andReturn()
        createGameResult.response.status shouldBe HttpStatus.OK.value()

        // when
        val gameCreated = mapper.readValue(createGameResult.response.contentAsString, GameCreatedDTO::class.java)

        // then
        val grid = getGrid(gameCreated.gameID)
        grid.grid.rows.size shouldBe 10
        grid.grid.rows.all { it.fields.all { it.state == UNKNOWN } }
    }

    @Test
    fun errorGettingNonExistingGame() {
        val gridResponse = mvc!!.perform(MockMvcRequestBuilders.get("/games/123123")).andReturn()
        gridResponse.response.status shouldBe HttpStatus.NOT_FOUND.value()
    }

    @Test
    fun errorWhenDiscoveringFieldOnNotExistingGame() {
        val discoverRequestContent = mapper.writeValueAsString(DiscoverFieldRequest(1, 1))

        val discoverResponse = mvc!!.perform(
            MockMvcRequestBuilders
                .post("/games/213321123/actions")
                .content(discoverRequestContent)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        discoverResponse.response.status shouldBe HttpStatus.NOT_FOUND.value()
    }

    @Test
    fun discoverFields() {
        val gameID = createGame()
        val discoverRequestContent = mapper.writeValueAsString(DiscoverFieldRequest(1, 1))

        // when sending discover request
        val discoverResponse = mvc!!.perform(
            MockMvcRequestBuilders
                .post("/games/${gameID}/actions")
                .content(discoverRequestContent)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        // then
        discoverResponse.response.status shouldBe HttpStatus.OK.value()
        val grid = getGrid(gameID)
        grid.grid.rows.first().fields.first().state shouldNotBe UNKNOWN
    }


    private fun createGame(): GameID {
        val createGameResult = mvc!!.perform(MockMvcRequestBuilders.post("/games")).andReturn()
        createGameResult.response.status shouldBe HttpStatus.OK.value()
        val gameCreated = mapper.readValue(createGameResult.response.contentAsString, GameCreatedDTO::class.java)
        return gameCreated.gameID
    }

    private fun getGrid(gameID: GameID): GameViewDTO {
        val gridResponse = mvc!!.perform(MockMvcRequestBuilders.get("/games/${gameID}")).andReturn()
        gridResponse.response.status shouldBe HttpStatus.OK.value()
        val grid = mapper.readValue(gridResponse.response.contentAsString, GameViewDTO::class.java)
        return grid
    }
}
