package com.gmiejski.minesweeper.integration.serialization

import com.gmiejski.minesweeper.game.domain.FieldCoordinate
import com.gmiejski.minesweeper.game.domain.GameCreatedEvent
import com.gmiejski.minesweeper.infrastructure.EventSerializerConfiguration
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SerializationTest {

    @Test
    fun serializeAndDeserializeGameStartedEvent() {
        val gameCreatedEvent = GameCreatedEvent("1", 10, 10, setOf(FieldCoordinate(3, 3)))

        val serializer = EventSerializerConfiguration().eventSerializer()

        val (byteBuffer, eventName) = serializer.serialize(gameCreatedEvent)

        val deserializezEvent = serializer.deserialize(byteBuffer.array(), eventName)

        deserializezEvent.getID() shouldBe "1"
        deserializezEvent shouldBe gameCreatedEvent
    }
}