package com.mkdn.ktor_mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform