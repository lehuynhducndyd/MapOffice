package com.example.mapoffice.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay

class KtorMapsApi(private val client: HttpClient) : MapsApi {
    private val baseUrl = "http://100.64.68.130:3000/api"

    override suspend fun getRooms(): List<RoomDto> {
        return client.get("$baseUrl/rooms").body()
    }

    override suspend fun getRoom(id: Int): RoomDto {
        return client.get("$baseUrl/rooms/$id").body()
    }

    override suspend fun createRoom(request: CreateRoomRequest) {
        client.post("$baseUrl/rooms") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun deleteRoom(id: Int) {
        client.delete("$baseUrl/rooms/$id")
    }

    override suspend fun updateRoom(
        id: Int,
        request: UpdateRoomRequest
    ) {
        val response = client.patch("$baseUrl/rooms/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun getGroups(): List<UserGroupDto> {
        return client.get("$baseUrl/groups").body()
    }

    override suspend fun getGroup(id: Int): UserGroupDto {
        return client.get("$baseUrl/groups/$id").body()
    }

    override suspend fun createGroup(request: CreateGroupRequest): UserGroupDto {
        val response = client.post("$baseUrl/groups") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun updateGroup(
        id: Int,
        request: CreateGroupRequest
    ): UserGroupDto {
        val response = client.patch("$baseUrl/groups/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun deleteGroup(id: Int): UserGroupDto {
        val response = client.delete("$baseUrl/groups/$id")
        return response.body()
    }

    override suspend fun getMapObjects(): List<MapObjectDto> {
        return client.get("$baseUrl/objects").body()
    }

    override suspend fun createMapObject(request: CreateMapObjectRequest): MapObjectDto {
        val response = client.post("$baseUrl/objects") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun updateMapObject(
        id: Int,
        request: UpdateMapObjectDTO
    ): MapObjectDto {
        val response = client.patch("$baseUrl/objects/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun deleteMapObject(id: Int): MapObjectDto {
        val response = client.delete("$baseUrl/objects/$id")
        return response.body()
    }

    override suspend fun createProduct(request: UpdateProductDTO): ProductDTO {
        val response = client.post("$baseUrl/products") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun updateProduct(
        id: Int,
        request: UpdateProductDTO
    ): ProductDTO {
        val response = client.patch("$baseUrl/products/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    override suspend fun deleteProduct(id: Int): ProductDTO {
        val response = client.delete("$baseUrl/products/$id")
        return response.body()
    }

    override suspend fun getProducts(): List<ProductDTO> {
        return client.get("$baseUrl/products").body()
    }

    override suspend fun getProduct(id: Int): ProductDTO {
        return client.get("$baseUrl/products/$id").body()
    }
}
