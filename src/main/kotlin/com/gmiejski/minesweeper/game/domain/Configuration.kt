package com.gmiejski.minesweeper.game.domain

class Configuration {
}

fun build(): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(RandomGenerator()))
}

fun buildForTest(generator: RandomGenerator): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(generator))
}