package com.berg.airapp.day2.api

import com.berg.airapp.day2.api.dto.FormatRequest
import com.berg.airapp.day2.api.dto.FormatResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FormatApi(
    private val client: HttpClient,
    private val apiKey: String
) {
    companion object {
        private const val BASE_URL = "https://api.anthropic.com/v1/messages"
        private const val ANTHROPIC_VERSION = "2023-06-01"
    }

    suspend fun sendMessage(request: FormatRequest): String {
        val response = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("x-api-key", apiKey)
            header("anthropic-version", ANTHROPIC_VERSION)
            setBody(request)
        }
        if (response.status.value !in 200..299) {
            val errorBody = response.body<String>()
            throw Exception("API error ${response.status.value}: $errorBody")
        }
        return response.body<FormatResponse>().content.firstOrNull()?.text ?: ""
    }
}
