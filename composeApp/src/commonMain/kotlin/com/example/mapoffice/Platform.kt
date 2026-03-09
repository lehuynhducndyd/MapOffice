package com.example.mapoffice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform