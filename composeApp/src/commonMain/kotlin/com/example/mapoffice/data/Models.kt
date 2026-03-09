package com.example.mapoffice.data

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ==========================================
// 1. ROOM (PHÒNG)
// ==========================================
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RoomDto(
    val id: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val createdAt: String,
    @EncodeDefault
    val mapObjects: List<MapObjectDto>? = null
)

// Dùng khi tạo mới (POST)
@Serializable
data class CreateRoomRequest(
    val name: String,
    val width: Int = 1000,
    val height: Int = 800
)

@Serializable
data class UpdateRoomRequest(
    val name: String,
    val width: Int,
    val height: Int
)

// ==========================================
// 2. USER GROUP (NHÓM NGƯỜI DÙNG)
// ==========================================
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UserGroupDto(
    val id: Int,
    val name: String,
    val colorCode: String? = "#CCCCCC",
    val description: String? = null,
    @EncodeDefault
    val mapObjects: List<MapObjectDto>? = null
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val colorCode: String? = "#CCCCCC",
    val description: String? = null
)

// Dùng khi tạo mới
@Serializable
data class CreateUserGroupRequest(
    val name: String,
    val colorCode: String? = "#CCCCCC",
    val description: String? = null
)

// ==========================================
// 3. MAP OBJECT (ĐỐI TƯỢNG BẢN ĐỒ)
// ==========================================

@Serializable
data class MapObjectDto(
    val id: Int,
    val roomId: Int,
    val groupId: Int? = null,
    val productId: Int? = null,
    val productTableId: Int? = null,
    val objectType: String,
    val posX: Int,
    val posY: Int,
    val width: Int,
    val height: Int,
    val isRotated: Boolean = false,
    val properties: JsonObject? = null,
    val createdAt: String,
    val userGroup: JsonObject? = null,
    val product: JsonObject? = null,
    val productTable: JsonObject? = null
)

// Dùng khi tạo mới Object (POST)
@Serializable
data class CreateMapObjectRequest(
    val roomId: Int,
    val groupId: Int? = null,
    val productId: Int? = null,
    val productTableId: Int? = null,
    val objectType: String,
    val posX: Int = 0,
    val posY: Int = 0,
    val width: Int = 50,
    val height: Int = 50,
    val isRotated: Boolean = false,
    val properties: JsonObject? = null
)

// Dùng khi cập nhật vị trí (PATCH) - Ví dụ khi kéo thả
@Serializable
data class UpdateMapObjectPositionRequest(
    val posX: Int,
    val posY: Int
)

@Serializable
data class UpdateMapObjectDTO(
    val posX: Int,
    val posY: Int,
    val width: Int,
    val height: Int,
    val groupId: Int?,
    val productId: Int?,
    val productTableId: Int?,
    val properties: JsonObject?,
    val isRotated: Boolean
)

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    val price: Float,
    val type: String
)

@Serializable
data class UpdateProductDTO(
    val name: String,
    val price: Float,
    val type: String
)

