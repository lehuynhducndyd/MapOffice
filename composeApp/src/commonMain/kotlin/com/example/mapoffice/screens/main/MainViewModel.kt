package com.example.mapoffice.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapoffice.data.CreateRoomRequest
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.MapsApi
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UpdateRoomRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapOfficeState(
    val loadStatus: LoadStatus = LoadStatus.Initial,
    val rooms: List<RoomDto> = emptyList(),
    val currentRoom: RoomDto? = null,
)


class MainViewModel(private val mapsApi: MapsApi) : ViewModel() {
    private val _uiState = MutableStateFlow(MapOfficeState())
    val uiState = _uiState.asStateFlow()

    fun getRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val list = mapsApi.getRooms()
                //delay(500)
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, rooms = list) }

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

    fun getRoom(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val room = mapsApi.getRoom(id)
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, currentRoom = room) }
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

    fun addRoom(name: String, width: Int, height: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.createRoom(
                    CreateRoomRequest(
                        name = name,
                        width = width,
                        height = height,
                    )
                )
                val newList = mapsApi.getRooms()
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, rooms = newList) }
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

    fun deleteRoom(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.deleteRoom(id)
                val newList = mapsApi.getRooms()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        rooms = newList,
                        currentRoom = null
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

    fun updateRoom(id: Int, name: String, width: Int, height: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.updateRoom(
                    id, UpdateRoomRequest(
                        name = name,
                        width = width,
                        height = height,
                    )
                )
                val newCurrentRoom = mapsApi.getRoom(_uiState.value.currentRoom!!.id)

                val newList = mapsApi.getRooms()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        rooms = newList,
                        currentRoom = newCurrentRoom
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

}