package com.example.mapoffice.screens.product

import com.example.mapoffice.screens.group.AddGroupPopUp
import com.example.mapoffice.screens.group.DeleteGroupPopUp
import com.example.mapoffice.screens.group.EditGroupPopUp
import com.example.mapoffice.screens.group.GroupScreenViewModel
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
import com.example.mapoffice.ui_component.ProductList
import com.example.mapoffice.ui_component.RoomList
import kotlinx.serialization.builtins.ArraySerializer
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
fun ProductScreen(
    navController: NavController,
    viewModel: ProductScreenViewModel = koinViewModel(),
    padding: PaddingValues
) {
    LaunchedEffect(Unit) {
        viewModel.getProducts()
    }
    val state by viewModel.uiState.collectAsState()

    var addProductPopUpOpen by remember { mutableStateOf(false) }
    var editProductPopUpOpen by remember { mutableStateOf(false) }
    var deleteProductPopUpOpen by remember { mutableStateOf(false) }
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
            Scaffold { paddingValues ->
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    floatingActionButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            HorizontalFloatingToolbar(
                                modifier = Modifier.padding(bottom = padding.calculateBottomPadding() - paddingValues.calculateBottomPadding()),
                                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                                    toolbarContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                expanded = true,

                                content = {
                                    IconButton(
                                        onClick = {
                                            addProductPopUpOpen = false
                                            editProductPopUpOpen = false
                                            deleteProductPopUpOpen = true
                                        },
                                        enabled = state.currentProduct != null
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.ic_oui_delete_outline),
                                            "delete", tint = Red
                                        )
                                    }
                                    IconButton(onClick = {
                                        editProductPopUpOpen = true
                                        deleteProductPopUpOpen = false
                                        addProductPopUpOpen = false
                                    }, enabled = state.currentProduct != null) {
                                        Icon(
                                            painterResource(Res.drawable.ic_oui_compose_edit),
                                            "edit",
                                        )
                                    }

                                }
                            )
                            FloatingActionButton(
                                onClick = {
                                    addProductPopUpOpen = true
                                    editProductPopUpOpen = false
                                    deleteProductPopUpOpen = false
                                },
                                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Icon(painterResource(Res.drawable.ic_oui_add), "Add")
                            }
                        }
                    },
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("Manage Product") },
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
                        ProductList(
                            modifier = Modifier.padding(horizontal = 8.dp)
                                .fillMaxSize(), viewModel = viewModel,
                            navController
                        )
                    }
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
                                    addProductPopUpOpen = true
                                    editProductPopUpOpen = false
                                    deleteProductPopUpOpen = false
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
                                    addProductPopUpOpen = false
                                    editProductPopUpOpen = false
                                    deleteProductPopUpOpen = true
                                },
                                enabled = state.currentProduct != null
                            ) {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_delete_outline),
                                    "delete", tint = Red
                                )
                            }
                            IconButton(onClick = {
                                editProductPopUpOpen = true
                                deleteProductPopUpOpen = false
                                addProductPopUpOpen = false
                            }, enabled = state.currentProduct != null) {
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
                        title = { Text("Manage Product") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    )
                },
            ) { paddingValues ->
                Row(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    ProductList(
                        modifier = Modifier.padding(horizontal = 8.dp)
                            .fillMaxSize(), viewModel = viewModel,
                        navController
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = addProductPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            AddProductPopUp(
                onAddNewProduct = { name, type, price ->
                    viewModel.createProduct(name, type, price)
                    addProductPopUpOpen = false
                },
                onDimiss = { addProductPopUpOpen = false }

            )
        }
        AnimatedVisibility(
            visible = deleteProductPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            val productToDelete = state.currentProduct
            if (productToDelete != null) {
                DeleteProductPopUp(
                    onConfirmDelete = {
                        viewModel.deleteProduct(productToDelete.id)
                        deleteProductPopUpOpen = false
                    },
                    onDimiss = { deleteProductPopUpOpen = false },
                    name = productToDelete.name
                )
            }
        }
        AnimatedVisibility(
            visible = editProductPopUpOpen,
            modifier = Modifier.align(Alignment.Center).zIndex(2f).width(364.dp)
        ) {
            EditProductPopUp(
                currentProduct = state.currentProduct ?: return@AnimatedVisibility,
                onEditNewProduct = { id, name, width, height ->
                    viewModel.updateProduct(id, name, width, height)
                    editProductPopUpOpen = false

                }, onDimiss = { editProductPopUpOpen = false }
            )
        }
    }


}