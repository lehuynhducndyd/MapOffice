package com.example.mapoffice.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapoffice.data.CreateGroupRequest
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.MapObjectDto
import com.example.mapoffice.data.MapsApi
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UpdateProductDTO
import com.example.mapoffice.data.UserGroupDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductState(
    val loadStatus: LoadStatus = LoadStatus.Initial,
    val products: List<ProductDTO> = emptyList(),
    val currentProduct: ProductDTO? = null,
    val objects: List<MapObjectDto> = emptyList(),
)


class ProductScreenViewModel(private val mapsApi: MapsApi) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductState())
    val uiState = _uiState.asStateFlow()

    fun getProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val list = mapsApi.getProducts()
                val objects = mapsApi.getMapObjects()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        products = list,
                        objects = objects
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

    fun getProduct(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val product = mapsApi.getProduct(id)
                _uiState.update { state ->
                    state.copy(
                        loadStatus = LoadStatus.Success,
                        currentProduct = product
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


    fun createProduct(name: String, type: String, price: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newProduct = mapsApi.createProduct(
                    UpdateProductDTO(
                        name = name,
                        price = price,
                        type = type
                    )
                )
                val newList = mapsApi.getProducts()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        products = newList,
                        currentProduct = newProduct
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

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                mapsApi.deleteProduct(id)
                val newList = mapsApi.getProducts()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        products = newList,
                        currentProduct = null
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

    fun updateProduct(id: Int, name: String, type: String, price: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadStatus = LoadStatus.Loading) }
            try {
                val newProduct = mapsApi.updateProduct(
                    id,
                    UpdateProductDTO(
                        name = name,
                        price = price,
                        type = type
                    )
                )
                val newCurrentProduct = mapsApi.getProduct(_uiState.value.currentProduct!!.id)
                val newList = mapsApi.getProducts()
                _uiState.update {
                    it.copy(
                        loadStatus = LoadStatus.Success,
                        products = newList,
                        currentProduct = newCurrentProduct
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
