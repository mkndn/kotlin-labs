package com.mkdn.dao

import com.mkdn.models.Article

interface DaoFacade {
    suspend fun findAll(): List<Article>
    suspend fun findOne(id: Int): Article?
    suspend fun create(title: String, body: String): Article?
    suspend fun update(id: Int, title: String, body: String): Boolean
    suspend fun delete(id: Int): Boolean
}