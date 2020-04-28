package com.gmiejski.minesweeper.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmiejski.minesweeper.otherservice.api.OtherServiceData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class MineSweeperOtherApplicationTests @Autowired constructor(val mapper: ObjectMapper) : AbstractIT() {
    @Test
    fun contextLoads() {
        val value = mvc!!.perform(MockMvcRequestBuilders.get("/other"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()

        val result = mapper.readValue(value.response.contentAsString, OtherServiceData::class.java)
    }
}
