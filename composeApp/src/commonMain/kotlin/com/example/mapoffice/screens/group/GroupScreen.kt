package com.example.mapoffice.screens.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.theme.Red
import com.example.mapoffice.ui_component.CurrentGroupDetail
import com.example.mapoffice.ui_component.CurrentRoomDetail
import com.example.mapoffice.ui_component.GroupList
import com.example.mapoffice.ui_component.RoomList
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.architecture
import mapoffice.composeapp.generated.resources.ic_oui_add
import mapoffice.composeapp.generated.resources.ic_oui_compose_edit
import mapoffice.composeapp.generated.resources.ic_oui_delete_outline
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun GroupScreen(
    navController: NavController,
    viewModel: GroupScreenViewModel = koinViewModel(),
    padding: PaddingValues
) {
    LaunchedEffect(Unit) {
        viewModel.getGroups()
    }
    val state by viewModel.uiState.collectAsState()

    var addGroupPopUpOpen by remember { mutableStateOf(false) }
    var editGroupPopUpOpen by remember { mutableStateOf(false) }
    var deleteGroupPopUpOpen by remember { mutableStateOf(false) }
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
                            addGroupPopUpOpen = true
                            editGroupPopUpOpen = false
                            deleteGroupPopUpOpen = false
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                    ) {
                        Icon(painterResource(Res.drawable.ic_oui_add), "Add")
                    }
                },
                topBar = {
                    LargeTopAppBar(
                        title = { Text("Manage Groups") },
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
                    GroupList(
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
                                    addGroupPopUpOpen = true
                                    editGroupPopUpOpen = false
                                    deleteGroupPopUpOpen = false
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                            ) {
                                Icon(painterResource(Res.drawable.ic_oui_add), "Add")
                            }
                        },
                        content = {
                            IconButton(
                                onClick = {
                                    addGroupPopUpOpen = false
                                    editGroupPopUpOpen = false
                                    deleteGroupPopUpOpen = true
                                },
                                enabled = state.currentGroup != null
                            ) {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_delete_outline),
                                    "delete", tint = Red
                                )
                            }
                            IconButton(onClick = {
                                editGroupPopUpOpen = true
                                deleteGroupPopUpOpen = false
                                addGroupPopUpOpen = false
                            }, enabled = state.currentGroup != null) {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_compose_edit),
                                    "edit",
                                )
                            }

                        }
                    )
                },
                topBar = {
                    TopAppBar(
                        title = { Text("Manage Groups") },
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
                    GroupList(
                        modifier = Modifier.weight(38f)
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp),
                        viewModel = viewModel,
                        navController
                    )
                    CurrentGroupDetail(
                        modifier = Modifier.weight(62f)
                            .padding(horizontal = 16.dp),
                        viewModel = viewModel
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = addGroupPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            AddGroupPopUp(
                onAddNewGroup = { name, color, desc ->
                    viewModel.createGroup(name, color, desc)
                    addGroupPopUpOpen = false
                },
                onDimiss = { addGroupPopUpOpen = false }

            )
        }
        AnimatedVisibility(
            visible = deleteGroupPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            val groupToDelete = state.currentGroup
            if (groupToDelete != null) {
                DeleteGroupPopUp(
                    onConfirmDelete = {
                        viewModel.deleteGroup(groupToDelete.id)
                        deleteGroupPopUpOpen = false
                    },
                    onDimiss = { deleteGroupPopUpOpen = false },
                    name = groupToDelete.name
                )
            }
        }
        AnimatedVisibility(
            visible = editGroupPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            EditGroupPopUp(
                currentGroup = state.currentGroup ?: return@AnimatedVisibility,
                onEditGroup = { id, name, width, height ->
                    viewModel.updateGroup(id, name, width, height)
                    editGroupPopUpOpen = false

                }, onDimiss = { editGroupPopUpOpen = false }
            )
        }
    }


}