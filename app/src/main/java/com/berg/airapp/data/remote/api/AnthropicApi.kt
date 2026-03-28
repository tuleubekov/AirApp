package com.berg.airapp.data.remote.api

import com.berg.airapp.BuildConfig
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.AnthropicResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AnthropicApi(
    private val client: HttpClient
) {
    suspend fun sendMessage(request: AnthropicRequest): AnthropicResponse {
        return client.post("https://api.anthropic.com/v1/messages") {
            contentType(ContentType.Application.Json)
            header("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
            header("anthropic-version", "2023-06-01")
            setBody(request)
        }.body()
    }
}
