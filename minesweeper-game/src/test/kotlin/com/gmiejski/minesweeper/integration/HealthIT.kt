package com.gmiejski.minesweeper.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmiejski.minesweeper.integration.AbstractIT
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class HealthIT @Autowired constructor(val mapper: ObjectMapper) : AbstractIT() {

    @Test
    fun contextLoads() {
        mvc!!.perform(MockMvcRequestBuilders.get("/health"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json("""{"health": "OK"}"""))
    }
}
