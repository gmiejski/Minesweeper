package com.gmiejski.minesweeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MineSweeperApplication

fun main(args: Array<String>) {
	runApplication<MineSweeperApplication>(*args)
}
