package com.example.mapoffice.screens.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.ui_component.BottomPropertiesContent
import com.example.mapoffice.ui_component.DetailObject
import com.example.mapoffice.ui_component.RightToolbarContent
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.architecture
import mapoffice.composeapp.generated.resources.arrow_back
import mapoffice.composeapp.generated.resources.edit_note
import mapoffice.composeapp.generated.resources.table_view
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FloorEditor(
    roomId: Int,
    viewModel: EditorViewModel = koinViewModel(),
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        viewModel.init(roomId)
    }
    val density = LocalDensity.current
    val screenWidth = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val state by viewModel.uiState.collectAsState()
    var detailOpen by remember { mutableStateOf(false) }
    var rightToolbarOpen by remember { mutableStateOf(false) }
    var bottomToolbarOpen by remember { mutableStateOf(false) }
    var deletePopUpOpen by remember { mutableStateOf(false) }
    var numberPopUpOpen by remember { mutableStateOf(false) }
    if (state.selectedObjectIdList.isEmpty()) {
        numberPopUpOpen = false
    }
    if (state.selectedObjectId == 0) {
        deletePopUpOpen = false
    }
    Scaffold {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            if (state.loadStatus == LoadStatus.Loading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Transparent).zIndex(100f)) {
                    Box(
                        modifier = Modifier.background(Color.White).size(100.dp)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            CanvasScreen(
                viewModel,
                state,
                onDelete = { deletePopUpOpen = true },
                onOpenDetail = { detailOpen = true }
            )
            if (screenWidth < 600.dp) {
                AnimatedVisibility(
                    visible = rightToolbarOpen,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(340.dp)
                        .padding(16.dp).zIndex(1f)
                ) {
                    Surface(
                        // Padding để không dính sát mép
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        RightToolbarContent(
                            state,
                            viewModel,
                            isOpen = { rightToolbarOpen = !rightToolbarOpen },
                            navController = navController
                        )
                    }
                }
                AnimatedVisibility(
                    visible = deletePopUpOpen,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    DeleteObjectPopUp(
                        onConfirmDelete = {
                            viewModel.deleteMapObject(state.selectedObjectId)
                            deletePopUpOpen = false
                        },
                        onDimiss = { deletePopUpOpen = false },
                        name = state.currentRoom?.mapObjects?.find { it.id == state.selectedObjectId }?.objectType
                            ?: ""
                    )
                }
                AnimatedVisibility(
                    visible = detailOpen,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(400.dp)
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = 64.dp,
                            bottom = 16.dp
                        )
                        .zIndex(1f)
                ) {
                    Surface(
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        DetailObject(state, viewModel, { detailOpen = !detailOpen })
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .zIndex(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AnimatedVisibility(
                        visible = detailOpen,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp) // Tự thích ứng độ rộng
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            DetailObject(state, viewModel, { detailOpen = false })
                        }
                    }

                    AnimatedVisibility(
                        visible = rightToolbarOpen,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp)
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            RightToolbarContent(
                                state,
                                viewModel,
                                isOpen = { rightToolbarOpen = !rightToolbarOpen },
                                navController = navController
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = deletePopUpOpen,
                modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
            ) {
                DeleteObjectPopUp(
                    onConfirmDelete = {
                        viewModel.deleteMapObject(state.selectedObjectId)
                        deletePopUpOpen = false
                    },
                    onDimiss = { deletePopUpOpen = false },
                    name = state.currentRoom?.mapObjects?.find { it.id == state.selectedObjectId }?.objectType
                        ?: ""
                )
            }
            AnimatedVisibility(
                visible = numberPopUpOpen,
                modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
            ) {
                NumberPopUp(
                    viewModel,
                    { numberPopUpOpen = false }
                )
            }

            AnimatedVisibility(
                visible = bottomToolbarOpen,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(16.dp),
            ) {
                Surface(
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    BottomPropertiesContent(
                        viewModel,
                        { numberPopUpOpen = true },
                        { deletePopUpOpen = true })
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                IconButton(
                    onClick = {
                        viewModel.saveAll()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                ) {
                    Icon(
                        painterResource(Res.drawable.arrow_back),
                        contentDescription = "save and exit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(
                    onClick = { detailOpen = !detailOpen },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            if (detailOpen) Color.LightGray else Color.White,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        painterResource(Res.drawable.edit_note),
                        contentDescription = "view and edit"
                    )
                }
                IconButton(
                    onClick = { rightToolbarOpen = !rightToolbarOpen },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            if (rightToolbarOpen) Color.LightGray else Color.White,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        painterResource(Res.drawable.table_view),
                        contentDescription = "list object"
                    )
                }
                IconButton(
                    onClick = { bottomToolbarOpen = !bottomToolbarOpen },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            if (bottomToolbarOpen) Color.LightGray else Color.White,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        painterResource(Res.drawable.architecture),
                        contentDescription = "Tool"
                    )
                }
            }

        }
    }

}



