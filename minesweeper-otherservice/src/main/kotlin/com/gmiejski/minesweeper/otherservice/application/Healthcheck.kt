package com.gmiejski.minesweeper.otherservice.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Healthcheck {

    @GetMapping("/health")
    fun healthcheck(): ResponseEntity<HealthResponse> {
        return ResponseEntity.ok(HealthResponse("OK"))
    }
}

data class HealthResponse(val health: String)
