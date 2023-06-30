package com.mkdn.sbkotlinblog

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException


@Controller
class BlogController(private val articleRepository: ArticleRepository,
    private val blogProperties: BlogProperties) {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = blogProperties.title
        model["banner"] = blogProperties.banner
        model["articles"] = articleRepository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "blog"
    }

    @GetMapping("/article/{slug}")
    fun article(@PathVariable slug: String, model: Model): String {
        val article = articleRepository.findBySlug(slug)?.render()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article for slug $slug not found")
        model["title"] = article.title
        model["article"] = article
        return "article"
    }

    fun Article.render() = RenderedArticle(
        slug, title, headline, content, author, addedAt.format()
    )

    data class RenderedArticle(
        val slug: String,
        val title: String,
        val headline: String,
        val content: String,
        val author: User,
        val addedAt: String)
}