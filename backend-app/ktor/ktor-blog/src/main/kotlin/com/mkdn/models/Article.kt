package com.mkdn.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.io.Serializable

data class Article(val id: Int, val title: String, val body: String): Serializable

object Articles: Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 128)
    val body = varchar("body", 512)

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toModel() = Article(
    id = this[Articles.id],
    title = this[Articles.title],
    body = this[Articles.body]
)