package com.mkdn.dao

import com.mkdn.models.Article
import com.mkdn.dao.DatabaseFactory.dbQuery
import com.mkdn.models.Articles
import com.mkdn.models.toModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ArticleDao : DaoFacade {

    override suspend fun findAll(): List<Article> = dbQuery {
        Articles.selectAll().map { it.toModel() }
    }

    override suspend fun findOne(id: Int): Article? = dbQuery {
        Articles
            .select { Articles.id eq id }
            .map { it.toModel() }
            .singleOrNull()
    }

    override suspend fun create(title: String, body: String): Article? = dbQuery {
        val insertStatement = Articles
            .insert {
                it[Articles.title] = title
                it[Articles.body] = body
            }
        insertStatement.resultedValues?.singleOrNull()?.toModel()
    }

    override suspend fun update(id: Int, title: String, body: String): Boolean =
        Articles
            .update({Articles.id eq id}) {
                it[Articles.title] = title
                it[Articles.body] = body
            } > 0

    override suspend fun delete(id: Int): Boolean =
        Articles
            .deleteWhere { Articles.id eq id } > 0
}