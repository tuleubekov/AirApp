package com.berg.airapp.day1.api

import com.berg.airapp.day1.api.dto.ChatRequest
import com.berg.airapp.day1.api.dto.StreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json

class ChatApi(
    private val client: HttpClient,
    private val json: Json,
    private val apiKey: String
) {
    companion object {
        private const val BASE_URL = "https://api.anthropic.com/v1/messages"
        private const val ANTHROPIC_VERSION = "2023-06-01"
    }

    fun streamMessage(request: ChatRequest): Flow<String> = channelFlow {
        Log.d("ChatApi", "🌐 Отправляем запрос к API, apiKey длина=${apiKey.length}")
        try {
            client.preparePost(BASE_URL) {
                contentType(ContentType.Application.Json)
                header("x-api-key", apiKey)
                header("anthropic-version", ANTHROPIC_VERSION)
                setBody(request)
            }.execute { response ->
                Log.d("ChatApi", "📡 Получен ответ: ${response.status.value}")
                if (response.status.value !in 200..299) {
                    val errorBody = response.body<String>()
                    throw Exception("API error ${response.status.value}: $errorBody")
                }
                val channel = response.body<ByteReadChannel>()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (!line.startsWith("data: ")) continue
                    val data = line.removePrefix("data: ")
                    if (data == "[DONE]") break

                    runCatching { json.decodeFromString<StreamEvent>(data) }
                        .getOrNull()
                        ?.takeIf { it.type == "content_block_delta" && it.delta?.type == "text_delta" }
                        ?.delta?.text
                        ?.let { send(it) }
                }
                Log.d("ChatApi", "🏁 Стрим завершён")
            }
        } catch (e: Exception) {
            Log.e("ChatApi", "💥 Исключение: ${e.message}", e)
            throw e
        }
    }
}
