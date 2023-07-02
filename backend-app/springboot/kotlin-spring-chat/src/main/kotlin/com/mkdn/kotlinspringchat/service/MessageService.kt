package com.mkdn.kotlinspringchat.service

import kotlinx.coroutines.flow.Flow


interface MessageService {

    fun latest(): Flow<MessageVM>

    fun after(id: String): Flow<MessageVM>

    fun stream(): Flow<MessageVM>

    suspend fun post(messages: Flow<MessageVM>)
}