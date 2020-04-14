package com.gmiejski.minesweeper.game.domain

class Configuration {
}

fun build(): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(Generator()))
}