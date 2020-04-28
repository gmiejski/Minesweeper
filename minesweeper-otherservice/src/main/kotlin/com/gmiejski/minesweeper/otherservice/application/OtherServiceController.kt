package com.gmiejski.minesweeper.otherservice.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmiejski.minesweeper.otherservice.api.OtherServiceData
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class OtherServiceController(val objectMapper: ObjectMapper) {

    @GetMapping("/other")
    fun randomDataButImportant(): ResponseEntity<OtherServiceData> {
        val logger = LoggerFactory.getLogger(OtherServiceController::class.java)
        val otherServiceData = OtherServiceData(Random.nextInt())
        logger.error(objectMapper.writeValueAsString(otherServiceData))
        return ResponseEntity.ok(otherServiceData)
    }

}