package com.example.mapoffice.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.ui_component.CurrentRoomDetail
import com.example.mapoffice.ui_component.RoomList
import com.example.mapoffice.theme.Red
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.architecture
import mapoffice.composeapp.generated.resources.ic_oui_add
import mapoffice.composeapp.generated.resources.ic_oui_compose_edit
import mapoffice.composeapp.generated.resources.ic_oui_delete_outline
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = koinViewModel(),
    padding: PaddingValues
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getRooms()
    }
    var addRoomPopUpOpen by remember { mutableStateOf(false) }
    var editRoomPopUpOpen by remember { mutableStateOf(false) }
    var deleteRoomPopUpOpen by remember { mutableStateOf(false) }
    val windowSizeClass = calculateWindowSizeClass()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.loadStatus == LoadStatus.Loading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Transparent).zIndex(100f)) {
                Box(
                    modifier = Modifier.background(Color.White).size(100.dp)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
//                    CircularProgressIndicator()
                    Text("Loading...")
                }
            }
        }
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            addRoomPopUpOpen = true
                            editRoomPopUpOpen = false
                            deleteRoomPopUpOpen = false
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                    ) {
                        Icon(painterResource(Res.drawable.ic_oui_add), "Add")
                    }
                },

                topBar = {
                    LargeTopAppBar(
                        title = { Text("Manage Rooms") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        scrollBehavior = scrollBehavior,
                    )
                },

                ) { paddingValues ->
                Row(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    RoomList(
                        modifier = Modifier.padding(horizontal = 8.dp)
                            .fillMaxSize(), viewModel = viewModel,
                        navController
                    )
                }
            }
        } else {
            Scaffold(
                floatingActionButton = {
                    HorizontalFloatingToolbar(
                        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                            toolbarContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        expanded = true,
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    addRoomPopUpOpen = true
                                    editRoomPopUpOpen = false
                                    deleteRoomPopUpOpen = false
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                            ) {
                                Icon(painterResource(Res.drawable.ic_oui_add), "Add")
                            }
                        },
                        content = {
                            IconButton(onClick = {
                                addRoomPopUpOpen = false
                                editRoomPopUpOpen = false
                                deleteRoomPopUpOpen = true
                            }, enabled = state.currentRoom != null) {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_delete_outline),
                                    "delete", tint = Red
                                )
                            }
                            IconButton(onClick = {
                                editRoomPopUpOpen = true
                                deleteRoomPopUpOpen = false
                                addRoomPopUpOpen = false
                            }, enabled = state.currentRoom != null) {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_compose_edit),
                                    "edit",
                                )
                            }
                            IconButton(onClick = {
                                if (state.currentRoom == null) return@IconButton
                                navController.navigate("canvas/${state.currentRoom?.id}")
                            }, enabled = state.currentRoom != null) {
                                Icon(
                                    painterResource(Res.drawable.architecture),
                                    "to to canvas",
                                )
                            }

                        }
                    )
                },
                topBar = {
                    TopAppBar(
                        title = { Text("Manage Rooms") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    )
                },

                ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(it),
                ) {
                    RoomList(
                        modifier = Modifier.weight(38f)
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp),
                        viewModel = viewModel,
                        navController
                    )
                    CurrentRoomDetail(
                        modifier = Modifier.weight(62f)
                            .padding(horizontal = 16.dp),
                        viewModel = viewModel,
                        onViewPrice = {
                            navController.navigate("room/detail/price/${state.currentRoom?.id}")
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = addRoomPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            AddRoomPopUp(
                onAddNewRoom = { name, width, height ->
                    viewModel.addRoom(name, width, height)
                    addRoomPopUpOpen = false
                }, onDimiss = { addRoomPopUpOpen = false }
            )
        }
        AnimatedVisibility(
            visible = editRoomPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            EditRoomPopUp(
                currentRoom = state.currentRoom ?: return@AnimatedVisibility,
                onEditRoom = { id, name, width, height ->
                    viewModel.updateRoom(id, name, width, height)
                    editRoomPopUpOpen = false

                }, onDimiss = { editRoomPopUpOpen = false }
            )
        }
        AnimatedVisibility(
            visible = deleteRoomPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            val roomToDelete = state.currentRoom
            if (roomToDelete != null) {
                DeleteRoomPopUp(
                    onConfirmDelete = {
                        viewModel.deleteRoom(roomToDelete.id)
                        deleteRoomPopUpOpen = false
                    },
                    onDimiss = { deleteRoomPopUpOpen = false },
                    name = roomToDelete.name
                )
            }
        }

    }
}

