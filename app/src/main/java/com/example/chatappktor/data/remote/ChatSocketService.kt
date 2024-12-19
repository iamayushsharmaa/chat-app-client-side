package com.example.chatappktor.data.remote

import com.example.chatappktor.domain.model.Message
import com.example.chatappktor.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        username: String
    ) : Resource<Unit>

    suspend fun sendMessage(message : String)

    fun  observeMessages() : Flow<Message>

    suspend fun closeSession()

    companion object{
        const val BASE_URL= "ws://192.168.87.158.8082"
    }

    sealed class EndPoints(val url : String){
        object ChatSocket : EndPoints("$BASE_URL/chat-socket")
    }
}