package com.berg.airapp.day7.db

import com.berg.airapp.day7.agent.AgentMessage

interface MessageStorage {
    suspend fun loadAll(): List<AgentMessage>
    suspend fun save(message: AgentMessage)
    suspend fun clearAll()
}

class MessageStorageImpl(
    private val dao: MessageDao
) : MessageStorage {

    override suspend fun loadAll(): List<AgentMessage> =
        dao.getAll().map { AgentMessage(role = it.role, content = it.content) }

    override suspend fun save(message: AgentMessage) {
        dao.insert(
            MessageEntity(
                id = java.util.UUID.randomUUID().toString(),
                role = message.role,
                content = message.content,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearAll() = dao.clearAll()
}
