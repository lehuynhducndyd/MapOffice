package com.example.mapoffice.data

interface MapsApi {
    suspend fun getRooms(): List<RoomDto>
    suspend fun getRoom(id: Int): RoomDto
    suspend fun createRoom(request: CreateRoomRequest)
    suspend fun deleteRoom(id: Int)
    suspend fun updateRoom(id: Int, request: UpdateRoomRequest)
    suspend fun getGroups(): List<UserGroupDto>
    suspend fun getGroup(id: Int): UserGroupDto
    suspend fun createGroup(request: CreateGroupRequest): UserGroupDto
    suspend fun updateGroup(id: Int, request: CreateGroupRequest): UserGroupDto
    suspend fun deleteGroup(id: Int): UserGroupDto
    suspend fun getMapObjects(): List<MapObjectDto>
    suspend fun createMapObject(request: CreateMapObjectRequest): MapObjectDto
    suspend fun updateMapObject(id: Int, request: UpdateMapObjectDTO): MapObjectDto
    suspend fun deleteMapObject(id: Int): MapObjectDto

    suspend fun createProduct(request: UpdateProductDTO): ProductDTO
    suspend fun updateProduct(id: Int, request: UpdateProductDTO): ProductDTO
    suspend fun deleteProduct(id: Int): ProductDTO
    suspend fun getProducts(): List<ProductDTO>
    suspend fun getProduct(id: Int): ProductDTO
}
