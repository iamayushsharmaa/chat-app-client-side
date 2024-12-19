package com.example.chatappktor.presentation.chat

import com.example.chatappktor.domain.model.Message

data class ChatState(
    val message : List<Message> = emptyList(),
    val isLoading : Boolean = false
)
