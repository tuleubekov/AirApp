package com.berg.airapp.day1.domain

data class Message(
    val id: String,
    val role: MessageRole,
    val content: String
)

enum class MessageRole {
    USER, ASSISTANT
}
