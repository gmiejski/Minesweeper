package com.gmiejski.minesweeper.otherservice.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class KotlinModuleJackson() {
    @Autowired
    fun configureObjectMapper(mapper: ObjectMapper) {
        mapper.registerModule(KotlinModule())
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}