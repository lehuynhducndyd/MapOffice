package com.example.mapoffice.screens.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.mapoffice.screens.main.MainViewModel
import com.example.mapoffice.theme.Red
import com.example.mapoffice.ui_component.CurrentGroupDetail
import com.example.mapoffice.ui_component.CurrentRoomDetail
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.architecture
import mapoffice.composeapp.generated.resources.ic_oui_back
import mapoffice.composeapp.generated.resources.ic_oui_compose_edit
import mapoffice.composeapp.generated.resources.ic_oui_delete_outline
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailGroupScreen(
    groupId: Int,
    navController: NavHostController,
    viewModel: GroupScreenViewModel = koinViewModel(),
    padding: PaddingValues
) {
    LaunchedEffect(Unit) {
        viewModel.getGroup(groupId)
    }
    val state by viewModel.uiState.collectAsState()
    var editGroupPopUpOpen by remember { mutableStateOf(false) }
    var deleteGroupPopUpOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text("Detail Group") },
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
                    colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                        toolbarContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    expanded = true,
                    // 3. Các icon hành động phụ
                    content = {
                        IconButton(onClick = {
                            editGroupPopUpOpen = false
                            deleteGroupPopUpOpen = true
                        }) {
                            Icon(
                                painterResource(Res.drawable.ic_oui_delete_outline),
                                "delete", tint = Red
                            )
                        }
                        IconButton(onClick = {
                            editGroupPopUpOpen = true
                            deleteGroupPopUpOpen = false
                        }) {
                            Icon(
                                painterResource(Res.drawable.ic_oui_compose_edit),
                                "edit",
                            )
                        }

                    }
                )
            },
        ) {
            CurrentGroupDetail(
                modifier = Modifier.padding(it).padding(horizontal = 8.dp),
                viewModel = viewModel
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