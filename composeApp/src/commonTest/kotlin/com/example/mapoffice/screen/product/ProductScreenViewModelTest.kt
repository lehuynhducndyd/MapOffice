package com.example.mapoffice.screen.product

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.mapoffice.data.CreateGroupRequest
import com.example.mapoffice.data.FakeApi
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.data.UpdateProductDTO
import com.example.mapoffice.data.UserGroupDto
import com.example.mapoffice.screens.group.GroupScreenViewModel
import com.example.mapoffice.screens.product.ProductScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ProductScreenViewModelTest {
    private lateinit var fakeApi: FakeApi
    private lateinit var viewModel: ProductScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Khởi tạo đồ giả và nhét vào ViewModel
        fakeApi = FakeApi()
        viewModel = ProductScreenViewModel(mapsApi = fakeApi)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetProducts() = runTest {
        fakeApi.shouldSimulateNetworkError = false
        fakeApi.createProduct(UpdateProductDTO("Product 1", 10.0f, "TABLE"))
        fakeApi.createProduct(UpdateProductDTO("Product 2", 20.0f, "TABLE"))
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getProducts()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.products).hasSize(2)
            assertThat(successState.products[0].name).isEqualTo("Product 1")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetProductError() = runTest {
        fakeApi.shouldSimulateNetworkError = true

        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getProducts()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            assertThat(awaitItem().loadStatus).isInstanceOf(LoadStatus.Error::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetProduct() = runTest {
        fakeApi.shouldSimulateNetworkError = false
        fakeApi.productsDb.add(
            ProductDTO(
                1, "Product 1", 10f, "TABLE"
            )
        )
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getProduct(1)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.currentProduct?.name ?: "").isEqualTo("Product 1")
            cancelAndIgnoreRemainingEvents()
        }
    }
}