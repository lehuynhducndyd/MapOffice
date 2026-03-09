package com.example.mapoffice.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.theme.Red
import com.example.mapoffice.ui_component.CurrentRoomDetail
import kotlinx.datetime.Month
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.architecture
import mapoffice.composeapp.generated.resources.ic_oui_back
import mapoffice.composeapp.generated.resources.ic_oui_compose_edit
import mapoffice.composeapp.generated.resources.ic_oui_delete_outline
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailRoomScreen(
    roomId: Int,
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel(),
    padding: PaddingValues
) {
    LaunchedEffect(Unit) {
        viewModel.getRoom(roomId)
    }
    val state by viewModel.uiState.collectAsState()
    var editRoomPopUpOpen by remember { mutableStateOf(false) }
    var deleteRoomPopUpOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            //.padding(bottom = padding.calculateBottomPadding())
            topBar = {
                LargeTopAppBar(
                    title = { Text("Detail Rooms") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(painterResource(Res.drawable.ic_oui_back), null)
                        }
                    }
                )
            },
            floatingActionButton = {
                HorizontalFloatingToolbar(
                    //modifier = Modifier.shadow(),
                    colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                        toolbarContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    expanded = true,

                    // 3. Các icon hành động phụ
                    content = {
                        IconButton(onClick = {
                            editRoomPopUpOpen = false
                            deleteRoomPopUpOpen = true
                        }) {
                            Icon(
                                painterResource(Res.drawable.ic_oui_delete_outline),
                                "delete", tint = Red
                            )
                        }
                        IconButton(onClick = {
                            editRoomPopUpOpen = true
                            deleteRoomPopUpOpen = false
                        }) {
                            Icon(
                                painterResource(Res.drawable.ic_oui_compose_edit),
                                "edit",
                            )
                        }
                        IconButton(onClick = {
                            if (state.currentRoom == null) return@IconButton
                            navController.navigate("canvas/${state.currentRoom?.id}")
                        }) {
                            Icon(
                                painterResource(Res.drawable.architecture),
                                "to to canvas",
                            )
                        }

                    }
                )
            },
        ) {
            CurrentRoomDetail(
                modifier = Modifier.padding(it).padding(horizontal = 8.dp),
                viewModel = viewModel,
                onViewPrice = {
                    navController.navigate("room/detail/price/${state.currentRoom?.id}")
                }

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
                        navController.popBackStack()
                    },
                    onDimiss = { deleteRoomPopUpOpen = false },
                    name = roomToDelete.name
                )
            }
        }
    }
}