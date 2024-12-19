package com.example.chatappktor.data.remote

import com.example.chatappktor.domain.model.Message
import com.example.chatappktor.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive

class ChatSocketsImpl(
    private val client : HttpClient
) : ChatSockets {

    private var socket : WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {

       return try {
           socket = client.webSocketSession {
               url(ChatSockets.EndPoints.ChatSocket.url)
           }
           if (socket?.isActive == true){
               Resource.Success(Unit)
           }else{
               Resource.Error("Couldn't establish a connection")
           }
        }catch (e : Exception){
            e.printStackTrace()
           Resource.Error(e.localizedMessage ?: "Unknown Error")
        }

    }

    override suspend fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun observeMessages(): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun closeSession() {
        TODO("Not yet implemented")
    }
}