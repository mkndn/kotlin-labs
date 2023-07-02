package com.mkdn.kotlinspringchat

import com.mkdn.kotlinspringchat.repository.ContentType
import com.mkdn.kotlinspringchat.repository.Message
import com.mkdn.kotlinspringchat.service.MessageVM
import com.mkdn.kotlinspringchat.service.UserVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL

fun MessageVM.asDomainObject(contentType: ContentType = ContentType.MARKDOWN) = Message(
    content, contentType, sent,
    user.name, user.avatarImageLink.toString(),
    id
)

fun Message.asViewModel() = MessageVM(
    contentType.render(content), UserVM(username, URL(userAvatarImageLink)),
        sent, id
)

fun Flow<Message>.mapToViewModel() = map { it.asViewModel() }

fun ContentType.render(content: String) = when(this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavour = CommonMarkFlavourDescriptor()
        HtmlGenerator(content,
            MarkdownParser(flavour).buildMarkdownTreeFromString(content),
            flavour).generateHtml()
    }
}

fun MessageVM.asRendered(contentType: ContentType = ContentType.MARKDOWN) =
    this.copy(content = contentType.render(content))