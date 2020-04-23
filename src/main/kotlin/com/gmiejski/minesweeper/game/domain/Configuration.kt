package com.gmiejski.minesweeper.game.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

fun build(): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(RandomBombsCoordinatesGenerator()))
}

fun buildForTest(bombsCoordinatesGenerator: BombsCoordinatesGenerator): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(bombsCoordinatesGenerator))
}

@Configuration
class Configuration {

    @Bean
    fun gameService(): MineSweeperGameService {
        return build()
    }
}