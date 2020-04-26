package com.gmiejski.minesweeper.infrastructure

import com.gmiejski.minesweeper.game.domain.*
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ReadPreference
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.Binary
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


@ConfigurationProperties
@Configuration
@Profile(PROD, TEST)
class MongoConfiguration() {
    @Value("\${mongo.url}")
    lateinit var mongoUrl: String
}


@Component
@Profile(PROD, TEST)

class MongoEventsAccessor(configuration: MongoConfiguration) {
    val games = build(configuration)

    private fun build(configuration: MongoConfiguration): MongoCollection<MongoEventRow> {

        val pojoCodecRegistry: CodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build())
        val codecRegistry: CodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry)

        val connString = ConnectionString(
            configuration.mongoUrl
        )
        val settings: MongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .readPreference(ReadPreference.primaryPreferred())
            .retryWrites(true)
            .codecRegistry(codecRegistry)
            .applyToSocketSettings { settings -> settings.connectTimeout(2000, TimeUnit.MILLISECONDS) }
            .applyToClusterSettings { s -> s.serverSelectionTimeout(3000, TimeUnit.MILLISECONDS) }
            .build()
        val mongoClient: MongoClient = MongoClients.create(settings)
        val database: MongoDatabase = mongoClient.getDatabase("minesweeper")
        return database.getCollection("games", MongoEventRow::class.java)
    }

    fun save(gameID: GameID, serializedEvent: ByteBuffer, serializerUsed: String) {
        val mongoEventRow = MongoEventRow()
        mongoEventRow.gameID = gameID
        mongoEventRow.eventBlob = Binary(serializedEvent.array())
        mongoEventRow.eventMapper = serializerUsed
        games.insertOne(mongoEventRow)
    }

    fun getAll(gameID: GameID): List<MongoEventRow> {
        val toList = games.find(Filters.eq("game_id", gameID)).toList()
        return toList
    }
}

class MongoEventRow {
    @BsonProperty(value = "game_id")
    lateinit var gameID: String

    @BsonProperty(value = "event_blob")
    lateinit var eventBlob: Binary

    @BsonProperty(value = "eventMapper")
    lateinit var eventMapper: String
}

@Repository
@Profile(PROD, TEST)
class MongoEventStore(val accessor: MongoEventsAccessor, val eventSerializer: EventSerializer) : EventStore {

    override fun get(gameID: GameID): List<DomainEvent> {
        val rows = accessor.getAll(gameID)
        return rows.map { eventSerializer.deserialize(it.eventBlob.data, it.eventMapper) }
    }

    fun save(gameID: GameID, event: DomainEvent) {
        val (serializedEvent, serializedUsed) = eventSerializer.serialize(event)
        val saved = accessor.save(gameID, serializedEvent, serializedUsed)
    }

    override fun saveAll(gameID: GameID, events: List<DomainEvent>) {
        events.forEach { this.save(gameID, it) }
    }
}
