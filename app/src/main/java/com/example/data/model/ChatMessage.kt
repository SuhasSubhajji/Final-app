package com.example.data.model

import java.util.UUID

enum class ChatSender {
    USER,
    GEMINI
}

data class GroundingSource(
    val title: String,
    val url: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: GeminiModel? = null,
    val isError: Boolean = false,
    val isThinking: Boolean = false,
    val imageUri: String? = null,
    val imageBase64: String? = null,
    val imageMimeType: String? = "image/jpeg",
    val isSearchGrounded: Boolean = false,
    val searchQueries: List<String> = emptyList(),
    val groundingSources: List<GroundingSource> = emptyList()
)
