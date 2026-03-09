package com.example.mapoffice.screens.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapoffice.data.CreateGroupRequest
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.MapsApi
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UserGroupDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupState(
    val loadStatus: LoadStatus = LoadStatus.Initial,
    val groups: List<UserGroupDto> = emptyList(),
    val currentGroup: UserGroupDto? = null,
    val rooms: List<RoomDto> = emptyList(),
)


class GroupScreenViewModel(private val mapsApi: MapsApi) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupState())
    val uiState = _uiState.asStateFlow()

    fun getGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val list = mapsApi.getGroups()
                val roomList = mapsApi.getRooms()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        groups = list,
                        rooms = roomList
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

    fun getGroup(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val group = mapsApi.getGroup(id)
                _uiState.update { it.copy(loadStatus = LoadStatus.Success, currentGroup = group) }
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

    fun getRoomName(id: Int): String {
        _uiState.value.rooms.forEach {
            if (it.id == id) {
                return it.name
            }
        }
        return ""
    }

    fun createGroup(name: String, color: String, desc: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newGroup = mapsApi.createGroup(
                    CreateGroupRequest(
                        name = name,
                        colorCode = color,
                        description = desc
                    )
                )
                val newList = mapsApi.getGroups()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        groups = newList
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

    fun deleteGroup(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.deleteGroup(id)
                val newList = mapsApi.getGroups()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        groups = newList,
                        currentGroup = null
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

    fun updateGroup(id: Int, name: String, color: String, desc: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newGroup = mapsApi.updateGroup(
                    id,
                    CreateGroupRequest(
                        name = name,
                        colorCode = color,
                        description = desc
                    )
                )
                val newCurrentGroup = mapsApi.getGroup(_uiState.value.currentGroup!!.id)
                val newList = mapsApi.getGroups()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        groups = newList,
                        currentGroup = newCurrentGroup
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