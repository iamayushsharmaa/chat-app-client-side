package com.example.chatappktor.data.remote.dto

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class MessageSto(
    val text : String,
    val timestamp: Long,
    val username: String,
    val id : String

)
