package com.gmiejski.minesweeper

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(classes = [MineSweeperApplication::class])
@AutoConfigureMockMvc
class AppTest {
    @Autowired
    private val mvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun helloGradle() {
        mvc!!.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello Gradle!"))
    }
}