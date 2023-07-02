package com.mkdn.kotlinspringchat.service

import com.mkdn.kotlinspringchat.asDomainObject
import com.mkdn.kotlinspringchat.asRendered
import com.mkdn.kotlinspringchat.repository.MessageRepository
import com.mkdn.kotlinspringchat.asViewModel
import com.mkdn.kotlinspringchat.mapToViewModel
import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class PersistentMessageService(val messageRepository: MessageRepository): MessageService {

    private val sender = MutableSharedFlow<MessageVM>()

    override fun latest() = messageRepository.findLatest().mapToViewModel()

    override fun after(id: String) = messageRepository.findLatest(id).mapToViewModel()

    override fun stream(): Flow<MessageVM> = sender

    override suspend fun post(messages: Flow<MessageVM>) {
        messages
            .onEach { sender.emit(it.asRendered()) }
            .map { it.asDomainObject() }
            .let { messageRepository.saveAll(it) }
            .collect()
    }
}