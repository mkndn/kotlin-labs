package com.mkdn.sbkotlinblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class SbKotlinBlogApplication

fun main(args: Array<String>) {
	runApplication<SbKotlinBlogApplication>(*args)
}
