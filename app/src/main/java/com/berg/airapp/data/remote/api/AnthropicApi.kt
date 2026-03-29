package com.berg.airapp.data.remote.api

import com.berg.airapp.BuildConfig
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.StreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json

class AnthropicApi(
    private val client: HttpClient,
    private val json: Json
) {
    companion object {
        private const val BASE_URL = "https://api.anthropic.com/v1/messages"
        private const val ANTHROPIC_VERSION = "2023-06-01"
    }

    fun streamMessage(request: AnthropicRequest): Flow<String> = channelFlow {
        client.preparePost(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
            header("anthropic-version", ANTHROPIC_VERSION)
            setBody(request)
        }.execute { response ->
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
        }
    }
}
