package com.mkdn.sbkotlinlab

import org.springframework.data.repository.CrudRepository

interface MessageRepository: CrudRepository<Message, String> {
}