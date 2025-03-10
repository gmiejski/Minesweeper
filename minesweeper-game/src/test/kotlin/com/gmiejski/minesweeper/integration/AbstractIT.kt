package com.gmiejski.minesweeper.integration

import com.gmiejski.minesweeper.MineSweeperApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [MineSweeperApplication::class])
@AutoConfigureMockMvc
class AbstractIT {
    @Autowired
    protected val mvc: MockMvc? = null
}