package com.gmiejski.minesweeper.otherservice.application

import com.gmiejski.minesweeper.otherservice.api.OtherServiceData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class OtherServiceController {

    @GetMapping("/other")
    fun randomDataButImportant(): ResponseEntity<OtherServiceData> {
        return ResponseEntity.ok(OtherServiceData(Random.nextInt()))
    }

}