package com.mkdn.sbkotlinblog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class RepositoriesTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val articleRepository: ArticleRepository) {

    @Test
    fun `test find article by Id`() {
        val user = User("john_doe", "John", "Doe")
        entityManager.persist(user)
        val article = Article("lorem", "lorem", "dolor sit amet", user)
        entityManager.persist(article)
        entityManager.flush()

        val found = articleRepository.findByIdOrNull(article.id!!)
        assertThat(found).isEqualTo(article)
    }

    @Test
    fun `test find by login`(){
        val user = User("john_doe", "John", "Doe")
        entityManager.persist(user)
        entityManager.flush()

        val found = userRepository.findByLogin(user.login)
        assertThat(found).isEqualTo(user)
    }
}