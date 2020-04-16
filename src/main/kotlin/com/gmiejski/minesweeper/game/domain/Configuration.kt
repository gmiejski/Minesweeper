package com.gmiejski.minesweeper.game.domain

fun build(): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(RandomBombsCoordinatesGenerator()))
}

fun buildForTest(bombsCoordinatesGenerator: BombsCoordinatesGenerator): MineSweeperGameService {
    return MineSweeperGameService(GameRepository(EventHandler(), InMemoryEventStore()), GameCommandHandler(bombsCoordinatesGenerator))
}