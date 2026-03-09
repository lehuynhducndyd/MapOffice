package com.example.mapoffice.data

import kotlinx.coroutines.delay


class FakeApi : MapsApi {

    // ==========================================
    // 1. CÁC "CÔNG TẮC" ĐỂ ĐIỀU KHIỂN BÀI TEST
    // ==========================================
    var shouldSimulateNetworkError = false
    var networkDelayMillis: Long = 0 // Tăng lên nếu muốn test loading spinner

    // ==========================================
    // 2. DATABASE TRÊN RAM (IN-MEMORY DB)
    // ==========================================
    val roomsDb = mutableListOf<RoomDto>()
    val groupsDb = mutableListOf<UserGroupDto>()
    val objectsDb = mutableListOf<MapObjectDto>()
    val productsDb = mutableListOf<ProductDTO>()

    // Bộ đếm ID tự động tăng (giống auto-increment trong SQL)
    private var roomIdCounter = 1
    private var groupIdCounter = 1
    private var objectIdCounter = 1
    private var productIdCounter = 1

    // Hàm tiện ích để giả lập mạng
    private suspend fun simulateNetwork() {
        if (networkDelayMillis > 0) delay(networkDelayMillis)
        if (shouldSimulateNetworkError) {
            throw Exception("Giả lập lỗi mạng (Timeout, 500 Server Error, v.v...)")
        }
    }

    // ==========================================
    // 3. IMPLEMENTATION CÁC HÀM CỦA ROOM
    // ==========================================
    override suspend fun getRooms(): List<RoomDto> {
        simulateNetwork()
        return roomsDb.toList()
    }

    override suspend fun getRoom(id: Int): RoomDto {
        simulateNetwork()
        return roomsDb.find { it.id == id } ?: throw Exception("Room not found")
    }

    override suspend fun createRoom(request: CreateRoomRequest) {
        simulateNetwork()
        val newRoom = RoomDto(
            id = roomIdCounter++,
            name = request.name,
            width = request.width,
            height = request.height,
            createdAt = "2024-01-01T00:00:00Z" // Fake thời gian
        )
        roomsDb.add(newRoom)
    }

    override suspend fun updateRoom(id: Int, request: UpdateRoomRequest) {
        simulateNetwork()
        val index = roomsDb.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldRoom = roomsDb[index]
            roomsDb[index] = oldRoom.copy(
                name = request.name,
                width = request.width,
                height = request.height
            )
        } else throw Exception("Room not found")
    }

    override suspend fun deleteRoom(id: Int) {
        simulateNetwork()
        roomsDb.removeAll { it.id == id }
    }

    // ==========================================
    // 4. IMPLEMENTATION CÁC HÀM CỦA GROUP
    // ==========================================
    override suspend fun getGroups(): List<UserGroupDto> {
        simulateNetwork()
        return groupsDb.toList()
    }

    override suspend fun getGroup(id: Int): UserGroupDto {
        simulateNetwork()
        return groupsDb.find { it.id == id } ?: throw Exception("Group not found")
    }

    override suspend fun createGroup(request: CreateGroupRequest): UserGroupDto {
        simulateNetwork()
        val newGroup = UserGroupDto(
            id = groupIdCounter++,
            name = request.name,
            colorCode = request.colorCode,
            description = request.description
        )
        groupsDb.add(newGroup)
        return newGroup
    }

    override suspend fun updateGroup(id: Int, request: CreateGroupRequest): UserGroupDto {
        simulateNetwork()
        val index = groupsDb.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedGroup = groupsDb[index].copy(
                name = request.name,
                colorCode = request.colorCode,
                description = request.description
            )
            groupsDb[index] = updatedGroup
            return updatedGroup
        } else throw Exception("Group not found")
    }

    override suspend fun deleteGroup(id: Int): UserGroupDto {
        simulateNetwork()
        val group = groupsDb.find { it.id == id } ?: throw Exception("Group not found")
        groupsDb.remove(group)
        return group
    }

    // ==========================================
    // 5. IMPLEMENTATION MAP OBJECTS & PRODUCTS
    // ==========================================
    override suspend fun getMapObjects(): List<MapObjectDto> {
        simulateNetwork()
        return objectsDb.toList()
    }

    override suspend fun createMapObject(request: CreateMapObjectRequest): MapObjectDto {
        simulateNetwork()
        val newObj = MapObjectDto(
            id = objectIdCounter++,
            roomId = request.roomId,
            groupId = request.groupId,
            productId = request.productId,
            productTableId = request.productTableId,
            objectType = request.objectType,
            posX = request.posX,
            posY = request.posY,
            width = request.width,
            height = request.height,
            isRotated = request.isRotated,
            properties = request.properties,
            createdAt = "2024-01-01T00:00:00Z"
        )
        objectsDb.add(newObj)
        return newObj
    }

    override suspend fun updateMapObject(id: Int, request: UpdateMapObjectDTO): MapObjectDto {
        simulateNetwork()
        val index = objectsDb.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = objectsDb[index].copy(
                posX = request.posX,
                posY = request.posY,
                width = request.width,
                height = request.height,
                groupId = request.groupId,
                productId = request.productId,
                productTableId = request.productTableId,
                properties = request.properties,
                isRotated = request.isRotated
            )
            objectsDb[index] = updated
            return updated
        } else throw Exception("Map Object not found")
    }

    override suspend fun deleteMapObject(id: Int): MapObjectDto {
        simulateNetwork()
        val obj = objectsDb.find { it.id == id } ?: throw Exception("Map Object not found")
        objectsDb.remove(obj)
        return obj
    }

    override suspend fun getProducts(): List<ProductDTO> {
        simulateNetwork()
        return productsDb.toList()
    }

    override suspend fun getProduct(id: Int): ProductDTO {
        simulateNetwork()
        return productsDb.find { it.id == id } ?: throw Exception("Product not found")
    }

    override suspend fun createProduct(request: UpdateProductDTO): ProductDTO {
        simulateNetwork()
        val newProduct = ProductDTO(
            id = productIdCounter++,
            name = request.name,
            price = request.price,
            type = request.type
        )
        productsDb.add(newProduct)
        return newProduct
    }

    override suspend fun updateProduct(id: Int, request: UpdateProductDTO): ProductDTO {
        simulateNetwork()
        val index = productsDb.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = productsDb[index].copy(
                name = request.name,
                price = request.price,
                type = request.type
            )
            productsDb[index] = updated
            return updated
        } else throw Exception("Product not found")
    }

    override suspend fun deleteProduct(id: Int): ProductDTO {
        simulateNetwork()
        val product = productsDb.find { it.id == id } ?: throw Exception("Product not found")
        productsDb.remove(product)
        return product
    }
}