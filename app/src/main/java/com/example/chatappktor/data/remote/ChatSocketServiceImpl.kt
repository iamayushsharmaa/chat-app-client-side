package com.example.chatappktor.data.remote

import com.example.chatappktor.data.remote.dto.MessageDto
import com.example.chatappktor.domain.model.Message
import com.example.chatappktor.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client : HttpClient
) : ChatSocketService {

    private var socket : WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {

       return try {
           socket = client.webSocketSession {
               url(ChatSocketService.EndPoints.ChatSocket.url)
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
        try {
            socket?.send(Frame.Text(message))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun observeMessages(): Flow<Message> {
        return flow {
            try {
                socket?.incoming
                    ?.receiveAsFlow()
                    ?.filter { it is Frame.Text } // Filter only text frames
                    ?.map { frame ->
                        val json = (frame as? Frame.Text)?.readText() ?: ""
                        val messageDto = Json.decodeFromString<MessageDto>(json)
                        messageDto.toMessage() // Map to your domain model
                    }
                    ?.collect { message ->
                        emit(message) // Emit each message into the flow
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.catch { exception ->
            // Catch and handle exceptions within the flow
            exception.printStackTrace()
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}