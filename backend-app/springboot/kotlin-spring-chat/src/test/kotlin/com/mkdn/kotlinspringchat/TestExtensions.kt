package com.mkdn.kotlinspringchat

import com.mkdn.kotlinspringchat.repository.Message
import com.mkdn.kotlinspringchat.service.MessageVM
import java.time.temporal.ChronoUnit.MILLIS

fun MessageVM.sanitize() = copy(id = null, sent = sent.truncatedTo(MILLIS))

fun Message.sanitize() = copy(id = null, sent = sent.truncatedTo(MILLIS))