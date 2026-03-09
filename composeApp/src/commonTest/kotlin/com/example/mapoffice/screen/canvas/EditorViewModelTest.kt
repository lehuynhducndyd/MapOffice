package com.example.mapoffice.screen.canvas

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.mapoffice.data.CreateGroupRequest
import com.example.mapoffice.data.CreateRoomRequest
import com.example.mapoffice.data.FakeApi
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UpdateProductDTO
import com.example.mapoffice.data.UserGroupDto
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.screens.group.GroupScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class EditorViewModelTest {
    private lateinit var fakeApi: FakeApi
    private lateinit var viewModel: EditorViewModel
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeApi = FakeApi()
        viewModel = EditorViewModel(mapsApi = fakeApi)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetGroups() = runTest {
        fakeApi.shouldSimulateNetworkError = false
        fakeApi.createGroup(CreateGroupRequest("Group 1", "#CCCCCC", ""))
        fakeApi.createGroup(CreateGroupRequest("Group 2", "#CCCCCC", ""))

        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getGroups()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.groups).hasSize(2)
            assertThat(successState.groups[0].name).isEqualTo("Group 1")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetRooms() = runTest {
        fakeApi.shouldSimulateNetworkError = false
        fakeApi.roomsDb.add(
            RoomDto(
                1, "Phòng Họp 1", 100, 100,
                createdAt = "2024-01-01T00:00:00Z",
                mapObjects = emptyList(),
            )
        )
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getRoom(1)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.currentRoom?.name ?: "").isEqualTo("Phòng Họp 1")

            cancelAndIgnoreRemainingEvents()
        }
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
    fun testSelectedObjId() = runTest {
        fakeApi.shouldSimulateNetworkError = false

        viewModel.uiState.test {
            assertThat(awaitItem().selectedObjectId).isEqualTo(0)
            viewModel.setSelectedObjectId(2)
            val successState = awaitItem()
            assertThat(successState.selectedObjectId).isEqualTo(2)

            cancelAndIgnoreRemainingEvents()
        }
    }


}