package com.example.mapoffice.utils

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test

class JsonExtensionTest {
    private val mockJson = buildJsonObject {
        put("string", "abcd")
        put("int", 10)
        put("bool", true)
        putJsonObject("json") {
            put("floor", 2)
        }
        // Cố tình bỏ vào một giá trị sai kiểu để test lỗi
        put("wrongInt", "không phải số")
    }

    @Test
    fun testGetString() {
        val value = mockJson.getString("string")
        assertThat(value).isEqualTo("abcd")
    }

    @Test
    fun testGetInt() {
        val value = mockJson.getInt("int")
        assertThat(value).isEqualTo(10)
    }

    @Test
    fun testGetBoolean() {
        val value = mockJson.getBoolean("bool")
        assertThat(value).isEqualTo(true)
    }

    @Test
    fun testGetJson() {
        val value = mockJson.getJson("json")
        //assertThat(value.getInt("floor")).isEqualTo(2)
        assertThat(value).isEqualTo(buildJsonObject { put("floor", 2) })
    }

    @Test
    fun testGetStringWithDefaultValue() {
        val value = mockJson.getString("string2", "default")
        assertThat(value).isEqualTo("default")
    }

    @Test
    fun getColor6Char() {
        val value = "#123456".toColor()
        assertThat(value).isEqualTo(Color(0xFF123456))

    }

    @Test
    fun getColor8Char() {
        val value = "#12345678".toColor()
        assertThat(value).isEqualTo(Color(0x12345678))

    }

}