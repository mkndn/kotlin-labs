package com.mkdn.sbkotlinlab

import org.springframework.stereotype.Service
import kotlin.jvm.optionals.toList

@Service
class MessageService(val messageRepository: MessageRepository) {

    fun findMessages(): List<Message> = messageRepository.findAll().toList()

    fun findMessageById(id: String): List<Message> = messageRepository.findById(id).toList()

    fun saveMessage(message: Message) = messageRepository.save(message)
}