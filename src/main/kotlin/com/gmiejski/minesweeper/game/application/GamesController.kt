package com.gmiejski.minesweeper.game.application

import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.RestController


@RestController("/")
class GamesController {
    @GetMapping
    fun helloGradle(): String {
        return "Hello Gradle!"
    }
}