package com.example.mapoffice.screen.group

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.mapoffice.data.CreateGroupRequest
import com.example.mapoffice.data.CreateRoomRequest
import com.example.mapoffice.data.FakeApi
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.data.RoomDto
import com.example.mapoffice.data.UserGroupDto
import com.example.mapoffice.screens.group.GroupScreenViewModel
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

class GroupScreenViewModelTest {
    private lateinit var fakeApi: FakeApi
    private lateinit var viewModel: GroupScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Khởi tạo đồ giả và nhét vào ViewModel
        fakeApi = FakeApi()
        viewModel = GroupScreenViewModel(mapsApi = fakeApi)
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
    fun testGetGroupsError() = runTest {
        fakeApi.shouldSimulateNetworkError = true

        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getGroups()
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            assertThat(awaitItem().loadStatus).isInstanceOf(LoadStatus.Error::class)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetGroup() = runTest {
        fakeApi.shouldSimulateNetworkError = false
        fakeApi.groupsDb.add(
            UserGroupDto(
                1, "Group 1", "#CCCCCC", ""
            )
        )
     
        viewModel.uiState.test {
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Initial)
            viewModel.getGroup(1)
            assertThat(awaitItem().loadStatus).isEqualTo(LoadStatus.Loading)
            val successState = awaitItem()
            assertThat(successState.loadStatus).isEqualTo(LoadStatus.Success)
            assertThat(successState.currentGroup?.name ?: "").isEqualTo("Group 1")
            cancelAndIgnoreRemainingEvents()
        }
    }
}