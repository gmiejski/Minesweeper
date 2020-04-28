package com.gmiejski.minesweeper.game.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.gmiejski.minesweeper.otherservice.api.OtherServiceData
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL


@RestController
class OtherServiceController @Autowired constructor(val otherServiceClient: OtherServiceClient) {

    @GetMapping("/other")
    fun getThatNumber(): ResponseEntity<OtherServiceData> {
        return ResponseEntity.ok(otherServiceClient.call())
    }
}

@Component
class OtherServiceClient(val otherServiceConfiguration: OtherServiceConfiguration, val objectMapper: ObjectMapper) {
    fun call(): OtherServiceData {
        val client: CloseableHttpClient = HttpClients.createDefault()
        val url = URL(URL(otherServiceConfiguration.otherServiceUrl), "other")
        val request = HttpGet(url.toURI())

        val response: CloseableHttpResponse = client.execute(request)
        val json: String = IOUtils.toString(response.entity.content)
        client.close()
        val logger = LoggerFactory.getLogger(OtherServiceController::class.java)
        logger.error(json)
        if (response.statusLine.statusCode == HttpStatus.OK.value()) {
            val importantData = objectMapper.readValue(json, OtherServiceData::class.java)
            return importantData
        }
        throw RuntimeException(response.toString())
    }
}

@Configuration
@ConfigurationProperties
class OtherServiceConfiguration {
    @Value("\${otherservice.url}")
    lateinit var otherServiceUrl: String
}
