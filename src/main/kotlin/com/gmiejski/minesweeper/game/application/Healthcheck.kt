package com.gmiejski.minesweeper.game.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Healthcheck {

    @GetMapping("/health")
    fun healthcheck(): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

}