package com.example.chatappktor.di

import com.example.chatappktor.data.remote.ChatSocketService
import com.example.chatappktor.data.remote.ChatSocketServiceImpl
import com.example.chatappktor.data.remote.MessageService
import com.example.chatappktor.data.remote.MessageServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient{
        return HttpClient(CIO){
            install(Logging)
            install(WebSockets)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true // Optional: For better readability of logs
                    isLenient = true   // Allows handling of slightly non-compliant JSON
                    ignoreUnknownKeys = true // Ignores unknown keys in the JSON response
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client : HttpClient) : MessageService{
        return MessageServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client : HttpClient) : ChatSocketService{
        return ChatSocketServiceImpl(client)
    }
}