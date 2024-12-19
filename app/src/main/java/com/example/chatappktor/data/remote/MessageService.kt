package com.example.chatappktor.data.remote

import com.example.chatappktor.domain.model.Message

interface MessageService {

    suspend fun getAllMessages() : List<Message>


    companion object{
        const val BASE_URL= "http://192.168.87.158.8082"
    }

    sealed class EndPoints(val url : String){
        object GetAllMessages : EndPoints("$BASE_URL/messages")
    }
}