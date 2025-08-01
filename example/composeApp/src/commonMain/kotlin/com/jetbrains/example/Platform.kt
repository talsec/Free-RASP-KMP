package com.jetbrains.example

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform