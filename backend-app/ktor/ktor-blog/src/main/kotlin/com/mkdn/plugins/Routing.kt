package com.mkdn.plugins

import com.mkdn.dao.ArticleDao
import com.mkdn.dao.CachedArticlesDao
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.util.*
import kotlinx.coroutines.runBlocking
import java.io.File

fun Application.configureRouting() {

    val dao = CachedArticlesDao(
        ArticleDao(),
        File(environment.config.property("storage.ehcacheFilePath").getString())
    ).apply {
        runBlocking {
            if (findAll().isEmpty()) {
                create("Factory Article", "Article created during application startup")
            }
        }
    }

    routing {
        staticResources("/static","assets")
        get("/") {
            call.respondRedirect("articles")
        }
        route("/articles") {
            get {
                call.respond(
                    FreeMarkerContent(
                        "index.ftl",
                        mapOf("articles" to dao.findAll())
                ))
            }
            get("/{id?}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "show_article.ftl",
                        mapOf("article" to dao.findOne(id))
                    )
                )
            }
            get("new") {
                call.respond(FreeMarkerContent(
                    "new_article.ftl",
                    model = null
                ))
            }
            get("/{id?}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "edit_article.ftl",
                        mapOf("article" to dao.findOne(id))
                    )
                )
            }
            post {
                val formData = call.receiveParameters()
                val article = dao.create(
                    title = formData.getOrFail("title"),
                    body = formData.getOrFail("body")
                )
                call.respondRedirect("/articles/${article?.id}")
            }
            post("/{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formData = call.receiveParameters()
                when(formData.getOrFail<String>("_action")) {
                    "update" -> {
                        val title = formData.getOrFail("title")
                        val body = formData.getOrFail("body")

                        dao.update(id, title, body)

                        call.respondRedirect("/articles/$id")
                    }
                    "delete" -> {
                        dao.delete(id)
                        call.respondRedirect("/articles")
                    }
                }
            }
        }
    }
}
