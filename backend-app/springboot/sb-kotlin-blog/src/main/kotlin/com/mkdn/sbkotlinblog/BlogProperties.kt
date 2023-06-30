package com.mkdn.sbkotlinblog

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("blog")
data class BlogProperties(val title: String, val banner: Banner) {
    data class Banner(val content: String, val title: String? = null)
}