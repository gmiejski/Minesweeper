package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.infrastructure.MongoConfiguration
import com.gmiejski.minesweeper.infrastructure.MongoEventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

//    @Bean
//    fun mongoEventStore(configuration: MongoConfiguration): MineSweeperGameService {
//        return build(configuration)
//    }
//
//    @Bean
//    @Profile(TEST)
//    fun gameServiceTest(bombsCoordinatesGenerator: BombsCoordinatesGenerator, configuration: MongoConfiguration): MineSweeperGameService {
//        return buildForIntegrationTest(bombsCoordinatesGenerator, configuration)
//    }

    @Bean
    @Profile(TEST)
    fun staticBombGenerator(): BombsCoordinatesGenerator {
        val bombs = listOf(FieldCoordinate(2, 2))
        return PredictableBombsCoordinatesGenerator(bombs)
    }
}

