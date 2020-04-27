package com.gmiejski.minesweeper.game.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

fun buildForTest(bombsCoordinatesGenerator: BombsCoordinatesGenerator? = null): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(bombsCoordinatesGenerator
        ?: RandomBombsCoordinatesGenerator()))
}

typealias ENVIRONMENT = String

const val PROD: ENVIRONMENT = "prod"
const val TEST: ENVIRONMENT = "integration"
const val LOCAL: ENVIRONMENT = "local"

@Configuration
class Configuration {
    @Bean
    @Profile(TEST)
    @Primary
    fun staticBombGenerator(): BombsCoordinatesGenerator {
        val bombs = listOf(FieldCoordinate(2, 2))
        return PredictableBombsCoordinatesGenerator(bombs)
    }
}

