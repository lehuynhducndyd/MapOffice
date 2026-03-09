package com.example.mapoffice.utils


import androidx.compose.ui.graphics.Color
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Hàm lấy String an toàn
fun JsonObject?.getString(key: String, default: String = ""): String {
    return this?.get(key)?.jsonPrimitive?.contentOrNull ?: default
}

fun JsonObject?.getJson(key: String): JsonObject {
    return this?.get(key)?.jsonObject ?: JsonObject(emptyMap())
}


// Hàm lấy Int an toàn
fun JsonObject?.getInt(key: String, default: Int = 0): Int {
    return this?.get(key)?.jsonPrimitive?.intOrNull ?: default
}

// Hàm lấy Boolean an toàn
fun JsonObject?.getBoolean(key: String, default: Boolean = false): Boolean {
    return this?.get(key)?.jsonPrimitive?.booleanOrNull ?: default
}

fun String.toColor(): Color? {
    return try {
        val cleanHex = this.trim().removePrefix("#")

        val colorLong = when (cleanHex.length) {
            6 -> "FF$cleanHex".toLong(16)
            8 -> cleanHex.toLong(16)
            else -> throw IllegalArgumentException("Invalid hex format")
        }

        Color(colorLong)
    } catch (e: Exception) {
        null
    }
}