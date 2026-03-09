package com.example.mapoffice.screens.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapoffice.data.CreateMapObjectRequest
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.MapObjectDto
import com.example.mapoffice.data.MapsApi
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UpdateMapObjectDTO
import com.example.mapoffice.data.UserGroupDto
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class EditorState(
    val loadStatus: LoadStatus = LoadStatus.Initial,
    val currentRoom: RoomDto? = null,
    val mapObjects: List<MapObjectDto> = emptyList(),
    val selectedObjectId: Int = 0,
    val groups: List<UserGroupDto> = emptyList(),
    val isStylus: Boolean = true,
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val centerX: Float = -100f,
    val centerY: Float = -100f,
    val selectedObjectIdList: List<Int> = emptyList(),
    val isSelectVertical: Boolean = true,
    val isLeftToRight: Boolean = true,
    val isTopToBottom: Boolean = true,
    val isBgImg: Boolean = false,
    val isShowBg: Boolean = false,
    val isShowGrid: Boolean = false,
    val bgBitmap: ImageBitmap? = null,
    val products: List<ProductDTO> = emptyList()
)

class EditorViewModel(private val mapsApi: MapsApi) : ViewModel() {
    private val _uiState = MutableStateFlow(EditorState())
    val uiState = _uiState.asStateFlow()

    fun getRoom(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val room = mapsApi.getRoom(id)
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        currentRoom = room,
                        mapObjects = room.mapObjects ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadStatus = LoadStatus.Error(e.message ?: "Error"))
                }
            }
        }
    }

    fun getGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val list = mapsApi.getGroups()
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, groups = list) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Error(
                            e.message.toString()
                        )
                    )
                }
            }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val list = mapsApi.getProducts()
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, products = list) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Error(
                            e.message.toString()
                        )
                    )
                }
            }
        }
    }

    fun init(id: Int) {
        getRoom(id)
        getGroups()
        getProducts()
    }

    fun setSelectedObjectId(id: Int) {
        _uiState.update { it.copy(selectedObjectId = id) }
    }

    fun updateLocalObject(id: Int, updated: UpdateMapObjectDTO) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }

                // 1. Gọi API
                mapsApi.updateMapObject(id, updated)

                _uiState.update { state ->
                    // 2. Tìm thông tin Group mới từ danh sách có sẵn để lấy màu (colorCode)
                    val newGroupInfo = state.groups.find { it.id == updated.groupId }
                    val newProductInfo = state.products.find { it.id == updated.productId }

                    val updatedList = state.mapObjects.map { obj ->
                        if (obj.id == id) {
                            obj.copy(
                                width = updated.width,
                                height = updated.height,
                                groupId = updated.groupId,
                                productId = updated.productId,
                                productTableId = updated.productTableId,
                                properties = updated.properties,
                                // Cập nhật userGroup để UI (Canvas) đổi màu ngay lập tức
                                userGroup = buildJsonObject {
                                    put("id", updated.groupId ?: 0)
                                    put("name", newGroupInfo?.name ?: "")
                                    put("colorCode", newGroupInfo?.colorCode ?: "")
                                },
                                isRotated = updated.isRotated,
                                product = buildJsonObject {
                                    put("id", updated.productId ?: 0)
                                    put("name", newProductInfo?.name ?: "")
                                    put("type", newProductInfo?.type ?: "")
                                },
                            )
                        } else obj
                    }
                    state.copy(loadStatus = LoadStatus.Success, mapObjects = updatedList)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loadStatus = LoadStatus.Error(e.message ?: "Lỗi")) }
            }
        }
    }

    fun updateObjectPositionOnly(id: Int, x: Int, y: Int) {
        val currentObj = _uiState.value.mapObjects.find { it.id == id } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.updateMapObject(
                    id = id,
                    UpdateMapObjectDTO(
                        posX = x,
                        posY = y,
                        width = currentObj.width,
                        height = currentObj.height,
                        groupId = currentObj.groupId,
                        productId = currentObj.productId,
                        productTableId = currentObj.productTableId,
                        properties = currentObj.properties,
                        isRotated = currentObj.isRotated
                    )
                )
                _uiState.update { state ->
                    state.copy(
                        mapObjects = state.mapObjects.map {
                            if (it.id == id)
                                it.copy(posX = x, posY = y)
                            else it
                        },
                        loadStatus = LoadStatus.Success
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Error(
                            e.message.toString()
                        )
                    )
                }
            }

        }

    }


    fun updateMapObject(id: Int, request: UpdateMapObjectDTO) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.updateMapObject(id, request)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Error(
                            e.message.toString()
                        )
                    )
                }
            }
        }
    }

    fun saveAll() {
        viewModelScope.launch {

            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                uiState.value.mapObjects.forEach {
                    mapsApi.updateMapObject(
                        it.id,
                        UpdateMapObjectDTO(
                            posX = it.posX,
                            posY = it.posY,
                            width = it.width,
                            height = it.height,
                            groupId = it.groupId,
                            productId = it.productId,
                            productTableId = it.productTableId,
                            properties = it.properties,
                            isRotated = it.isRotated
                        )
                    )
                }
                _uiState.update { it.copy(loadStatus = LoadStatus.Success) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Error(
                            e.message.toString()
                        )
                    )
                }
            }

        }
    }

    fun moveSelectedObject(
        dx: Float,
        dy: Float,
        worldW: Float,
        worldH: Float
    ) {
        val selectedId = uiState.value.selectedObjectId

        _uiState.update { state ->
            state.copy(
                mapObjects = state.mapObjects.map { obj ->
                    if (obj.id == selectedId) {
                        obj.copy(
                            posX = (obj.posX + dx)
                                .coerceIn(0f, worldW)
                                .toInt(),
                            posY = (obj.posY + dy)
                                .coerceIn(0f, worldH)
                                .toInt()
                        )
                    } else obj
                }
            )
        }
    }

    fun rotateSelectedObject() {
        val selectedId = uiState.value.selectedObjectId
        if (selectedId == 0) return

        _uiState.update { state ->
            val newObjects = state.mapObjects.map { obj ->
                if (obj.id == selectedId) {
                    obj.copy(
                        isRotated = !obj.isRotated,
                    )
                } else obj
            }
            state.copy(mapObjects = newObjects)
        }
    }

    fun addObject(type: String) {
        val roomId = _uiState.value.currentRoom?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newObject = when (type) {
                    "TABLE" ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                posX = _uiState.value.centerX.toInt(),
                                posY = _uiState.value.centerY.toInt(),
                                width = 120,
                                height = 60,
                                groupId = null,
                                roomId = roomId,
                                properties = buildJsonObject {
                                    put("userName", "Trống")
                                    put("seatNumber", "##")
                                },
                                objectType = type,
                            )
                        )

                    "LABEL" ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                objectType = type,
                                posX = _uiState.value.centerX.toInt(),
                                posY = _uiState.value.centerY.toInt(),
                                width = 240,
                                height = 30,
                                groupId = null,
                                roomId = roomId,
                                properties = buildJsonObject {
                                    put("text", "Nhãn mới")
                                }
                            )
                        )

                    else ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                objectType = "OBJECT",
                                posX = _uiState.value.centerX.toInt(),
                                posY = _uiState.value.centerY.toInt(),
                                width = 50,
                                height = 50,
                                groupId = null,
                                roomId = roomId,
                                properties = buildJsonObject {
                                    put("name", "object")
                                }
                            )
                        )
                }
                _uiState.update { state ->
                    state.copy(
                        loadStatus = LoadStatus.Success,
                        mapObjects = state.mapObjects + newObject,
                        selectedObjectId = newObject.id
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadStatus = LoadStatus.Error(e.message ?: "Lỗi khi thêm vật thể"))
                }
            }
        }
    }

    fun addObjectWithPos(x: Int, y: Int) {
        val currentobj =
            _uiState.value.mapObjects.find { it.id == _uiState.value.selectedObjectId } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newObject = when (currentobj.objectType) {
                    "TABLE" ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                posX = x,
                                posY = y,
                                width = currentobj.width,
                                height = currentobj.height,
                                groupId = currentobj.groupId,
                                productId = currentobj.productId,
                                productTableId = currentobj.productTableId,
                                roomId = currentobj.roomId,
                                properties = buildJsonObject {
                                    put("userName", "Trống")
                                    put("seatNumber", "##")
                                },
                                objectType = currentobj.objectType,
                                isRotated = currentobj.isRotated
                            )
                        )

                    "LABEL" ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                objectType = currentobj.objectType,
                                isRotated = currentobj.isRotated,
                                posX = x,
                                posY = y,
                                width = currentobj.width,
                                height = currentobj.height,
                                groupId = currentobj.groupId,
                                roomId = currentobj.roomId,
                                properties = buildJsonObject {
                                    put("text", "Nhãn mới")
                                },
                                productId = null,
                                productTableId = null
                            )
                        )

                    else ->
                        mapsApi.createMapObject(
                            CreateMapObjectRequest(
                                objectType = currentobj.objectType,
                                isRotated = currentobj.isRotated,
                                posX = x,
                                posY = y,
                                width = currentobj.width,
                                height = currentobj.height,
                                productId = currentobj.productId,
                                productTableId = currentobj.productTableId,
                                groupId = currentobj.groupId,
                                roomId = currentobj.roomId,
                                properties = buildJsonObject {
                                    put("name", "object")
                                }
                            )
                        )
                }
                _uiState.update { state ->
                    state.copy(
                        loadStatus = LoadStatus.Success,
                        mapObjects = state.mapObjects + newObject.copy(
                            userGroup = currentobj.userGroup,
                            product = currentobj.product
                        ),
                        selectedObjectId = newObject.id
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadStatus = LoadStatus.Error(e.message ?: "Lỗi khi thêm vật thể"))
                }
            }
        }
    }

    fun setIsStylus(isStylus: Boolean) {
        _uiState.update { it.copy(isStylus = isStylus) }
    }

    fun deleteMapObject(id: Int) {

        viewModelScope.launch {
            _uiState.update {
                it.copy(loadStatus = LoadStatus.Loading)
            }
            try {
                mapsApi.deleteMapObject(id)

                _uiState.update { state ->
                    val newList = state.mapObjects.filter { it.id != id }
                    state.copy(
                        loadStatus = LoadStatus.Success,
                        mapObjects = newList,
                        selectedObjectId = 0 // Reset lựa chọn
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadStatus = LoadStatus.Error(e.message ?: "Lỗi khi xóa vật thể"))
                }
            }

        }
    }

    fun setScale(scale: Float) {
        _uiState.update { it.copy(scale = scale) }
    }

    fun setOffset(offset: Offset) {
        _uiState.update { it.copy(offset = offset) }
    }

    fun setCenterX(x: Float) {
        _uiState.update { it.copy(centerX = x) }
    }

    fun setCenterY(y: Float) {
        _uiState.update { it.copy(centerY = y) }
    }

    fun setIsSelectVertical(isSelectVertical: Boolean) {
        _uiState.update { it.copy(isSelectVertical = isSelectVertical) }
    }

    fun setIsLeftToRight(isLeftToRight: Boolean) {
        _uiState.update {
            it.copy(isLeftToRight = isLeftToRight)
        }
    }

    fun setIsTopToBottom(isTopToBottom: Boolean) {
        _uiState.update {
            it.copy(isTopToBottom = isTopToBottom)
        }
    }

    fun setSelectedObjectIdList(selectedObjectIdList: List<Int>) {
        _uiState.update {
            it.copy(selectedObjectIdList = selectedObjectIdList)
        }
    }

    fun setSelectedObjectIdList() {
        val currentOnj = _uiState.value.mapObjects.find { it.id == _uiState.value.selectedObjectId }
        var list = emptyList<Int>()
        if (_uiState.value.isSelectVertical) {
            _uiState.value.mapObjects.filter { it.objectType == "TABLE" }.forEach {
                if (it.posX == currentOnj?.posX) {
                    list = list + it.id
                }
            }
            _uiState.update {
                it.copy(selectedObjectIdList = list)
            }
        } else {
            _uiState.value.mapObjects.filter { it.objectType == "TABLE" }.forEach {
                if (it.posY == currentOnj?.posY) {
                    list = list + it.id
                }
            }
            _uiState.update {
                it.copy(selectedObjectIdList = list)
            }
        }
    }

    fun numberedObject(start: Int) {
        var currentNumber = start
        val state = _uiState.value
        val selectedIds = state.selectedObjectIdList
        val isVertical = state.isSelectVertical
        val isLeftToRight = state.isLeftToRight
        val isTopToBottom = state.isTopToBottom // Giả sử bạn có biến này trong State

        val allObjects = state.mapObjects.toMutableList()

        // 1. Lọc và Sắp xếp linh hoạt
        val selectedObjects = allObjects
            .filter { it.id in selectedIds }
            .sortedBy { obj ->
                if (isVertical) {
                    // Nếu là cột dọc: Trên -> Xuống (posY) hoặc Dưới -> Trên (-posY)
                    if (isTopToBottom) obj.posY.toFloat() else -obj.posY.toFloat()
                } else {
                    // Nếu là hàng ngang: Trái -> Phải (posX) hoặc Phải -> Trái (-posX)
                    if (isLeftToRight) obj.posX.toFloat() else -obj.posX.toFloat()
                }
            }

        // 2. Tạo Map cập nhật dữ liệu
        val updatedObjectsMap = selectedObjects.associate { obj ->
            val newObj = obj.copy(
                properties = buildJsonObject {
                    // Copy lại các thuộc tính cũ để tránh mất dữ liệu
                    obj.properties?.forEach { (key, value) -> put(key, value) }
                    // Ghi đè hoặc thêm mới seatNumber
                    put("seatNumber", currentNumber.toString())
                }
            )
            currentNumber++
            newObj.id to newObj
        }

        // 3. Hợp nhất vào danh sách tổng
        val finalStateList = allObjects.map { originalObj ->
            updatedObjectsMap[originalObj.id] ?: originalObj
        }

        // 4. Cập nhật State
        _uiState.update {
            it.copy(mapObjects = finalStateList)
        }
    }

    fun duplicate(direction: Direction) {
        val currentObj = _uiState.value.mapObjects.find { it.id == _uiState.value.selectedObjectId }
            ?: _uiState.value.mapObjects[0]
        when (direction) {
            Direction.UP -> {
                addObjectWithPos(
                    currentObj.posX,
                    currentObj.posY - if (!currentObj.isRotated) currentObj.height else currentObj.width,
                )
            }

            Direction.DOWN -> {
                addObjectWithPos(
                    currentObj.posX,
                    currentObj.posY + if (!currentObj.isRotated) currentObj.height else currentObj.width,
                )
            }

            Direction.LEFT -> {
                addObjectWithPos(
                    currentObj.posX - if (!currentObj.isRotated) currentObj.width else currentObj.height,
                    currentObj.posY,
                )
            }

            Direction.RIGHT -> {
                addObjectWithPos(
                    currentObj.posX + if (!currentObj.isRotated) currentObj.width else currentObj.height,
                    currentObj.posY,
                )
            }

        }
    }

    fun setIsBgImg(isBgImg: Boolean) {
        _uiState.update {
            it.copy(
                isBgImg = isBgImg
            )
        }
    }

    fun setIsShowBg(isShowBg: Boolean) {
        _uiState.update {
            it.copy(
                isShowBg = isShowBg
            )
        }
    }

    fun setIsShowGrid(isShowGrid: Boolean) {
        _uiState.update {
            it.copy(
                isShowGrid = isShowGrid
            )
        }
    }

    fun setBackgroundImage(bitmap: ImageBitmap?) {
        _uiState.value = _uiState.value.copy(bgBitmap = bitmap)
        setIsShowBg(true)
        //setIsBgImg(true)
    }
}