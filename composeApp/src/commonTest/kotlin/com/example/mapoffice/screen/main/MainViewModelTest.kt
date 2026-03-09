package com.example.mapoffice.screen.main

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.example.mapoffice.data.CreateRoomRequest
import com.example.mapoffice.data.FakeApi
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.screens.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MainViewModelTest {
    private lateinit var fakeApi: FakeApi
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Khởi tạo đồ giả và nhét vào ViewModel
        fakeApi = FakeApi()
        viewModel = MainViewModel(mapsApi = fakeApi)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetRooms() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
        fakeApi.shouldSimulateNetworkError = false
        // Nhét 2 phòng giả vào Database RAM
        fakeApi.createRoom(CreateRoomRequest("Phòng Họp 1", 100, 100))
        fakeApi.createRoom(CreateRoomRequest("Phòng Họp 2", 200, 200))

        // 2. Act & Assert: Hứng dòng chảy State
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getRooms()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.rooms).hasSize(2)
            assertThat(successState.rooms[0].name).isEqualTo("Phòng Họp 1")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetRoomsError() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
        fakeApi.shouldSimulateNetworkError = true
        // Nhét 2 phòng giả vào Database RAM

        // 2. Act & Assert: Hứng dòng chảy State
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getRooms()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            assertThat(awaitItem().loadStatus).isInstanceOf(LoadStatus.Error::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetRoom() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
        fakeApi.shouldSimulateNetworkError = false
        // Nhét 2 phòng giả vào Database RAM
        fakeApi.roomsDb.add(
            RoomDto(
                1, "Phòng Họp 1", 100, 100,
                createdAt = "2024-01-01T00:00:00Z",
                mapObjects = emptyList(),
            )
        )
        // 2. Act & Assert: Hứng dòng chảy State
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
    fun testAddRoom() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
        fakeApi.shouldSimulateNetworkError = false

        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.addRoom("Phòng Họp 1", 100, 100)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.rooms.lastOrNull()?.name ?: "").isEqualTo("Phòng Họp 1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testUpdateRoom() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
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
            awaitItem()
            awaitItem()
            viewModel.updateRoom(1, "Phòng Họp 2", 100, 100)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.rooms.first { it.id == 1 }.name).isEqualTo("Phòng Họp 2")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testDeleteRoom() = runTest {
        // 1. Arrange: Ép API phải trả về thành công
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
            awaitItem()
            awaitItem()
            viewModel.deleteRoom(1)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.rooms.filter { it.id == 1 }.size).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}