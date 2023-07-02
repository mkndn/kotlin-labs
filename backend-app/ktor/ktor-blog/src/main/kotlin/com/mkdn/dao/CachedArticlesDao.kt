package com.mkdn.dao

import com.mkdn.models.Article
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

class CachedArticlesDao(
    private val delegate: DaoFacade,
    cacheFile: File
): DaoFacade {

    private val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(cacheFile))
        .withCache(
            "articlesCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Int::class.javaObjectType,
                Article::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        ).build(true)

    private val articlesCache = cacheManager.getCache(
        "articlesCache",
        Int::class.javaObjectType,
        Article::class.java)

    override suspend fun findAll(): List<Article> = delegate.findAll()

    override suspend fun findOne(id: Int): Article? =
        articlesCache[id] ?: delegate.findOne(id)
            ?.also { articlesCache.put(it.id, it) }

    override suspend fun create(title: String, body: String): Article? =
        delegate.create(title, body)?.also { articlesCache.put(it.id, it) }

    override suspend fun update(id: Int, title: String, body: String): Boolean =
        delegate.update(id, title, body).also {
            articlesCache.put(id, Article(id, title, body))
        }

    override suspend fun delete(id: Int): Boolean =
        delegate.delete(id).also { articlesCache.remove(id) }
}