package com.example.chatappktor.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappktor.data.remote.ChatSocketService
import com.example.chatappktor.data.remote.MessageService
import com.example.chatappktor.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _messageText = mutableStateOf<String>("")
    val messageText : State<String> = _messageText


    private val _state = mutableStateOf(ChatState())
    val state : State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        getAllMessage()
        savedStateHandle.get<String>("username")?.let { username->
            viewModelScope.launch {
               val result = chatSocketService.initSession(username)
                when(result){
                    is Resource.Success -> {
                        chatSocketService.observeMessages()
                            .onEach { message ->
                                val newList = state.value.message.toMutableList().apply {
                                    add(0,message)
                                }
                                _state.value.copy(
                                    message = newList
                                )
                            }.launchIn(viewModelScope)
                    }
                    is Resource.Error ->{
                          _toastEvent.emit(result.message ?: "Unknown Error")
                    }
                }
            }

        }
    }

    fun onMessageChange(message : String){
        _messageText.value = message
    }

    fun sendMessage(){
        viewModelScope.launch {
            if (messageText.value.isNotBlank()){
                chatSocketService.sendMessage(messageText.value)
            }
        }
    }

    fun getAllMessage(){
        viewModelScope.launch {
            _state.value.copy(isLoading = true)
            val result = messageService.getAllMessages()
            _state.value.copy(
                message = result,
                isLoading = false
            )
        }
    }
    fun disconnect(){
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()

        disconnect()
    }
}