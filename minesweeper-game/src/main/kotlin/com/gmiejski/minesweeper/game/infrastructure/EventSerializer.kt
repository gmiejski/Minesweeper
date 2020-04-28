package com.gmiejski.minesweeper.game.infrastructure

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer
import com.gmiejski.minesweeper.game.domain.DomainEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.concurrent.getOrSet

class EventSerializer(private val events: List<Class<out DomainEvent>>) {

    val myThreadLocal = ThreadLocal<Kryo>()

    val classByName = events.classesByNames()

    fun serialize(domainEvent: DomainEvent): Pair<ByteBuffer, String> {
        val output = Output(ByteArrayOutputStream())
        getKryo().writeObject(output, domainEvent)
        return Pair(ByteBuffer.wrap(output.buffer), domainEvent::class.java.simpleName)
    }

    fun deserialize(bytes: ByteArray, clazz: String): DomainEvent {
        val input = Input(bytes)
        return getKryo().readObject(input, classByName[clazz])
    }

    private fun getKryo() = myThreadLocal.getOrSet {
        val kryo = Kryo()
        events.forEach {
            kryo.register(it::class.java, TaggedFieldSerializer<DomainEvent>(kryo, it::class.javaObjectType))
        }
        return kryo
    }
}

private fun List<Class<out DomainEvent>>.classesByNames(): Map<String, Class<out DomainEvent>> {
    val pair = this.map { Pair(it.simpleName, it) }
    return mapOf(*pair.toTypedArray())
}

@Configuration
class EventSerializerConfiguration {

    @Bean
    fun eventSerializer(): EventSerializer {
        val allPossibleEvents = DomainEvent::class.sealedSubclasses.map { it.java }
        return EventSerializer(allPossibleEvents)
    }
}