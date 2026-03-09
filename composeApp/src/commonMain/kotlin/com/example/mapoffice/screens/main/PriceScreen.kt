package com.example.mapoffice.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavHostController
import com.example.mapoffice.data.LoadStatus
import com.example.mapoffice.ui_component.PriceTable
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.ic_oui_back
import org.jetbrains.compose.resources.painterResource

import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PriceScreen(
    roomId: Int,
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel(),
    padding: PaddingValues
) {
    LaunchedEffect(Unit) {
        viewModel.getRoom(roomId)
    }
    val state by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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

                topBar = {
                    LargeTopAppBar(
                        title = { Text("Total Price") },
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
            ) { paddingValues ->
                Row(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    PriceTable(viewModel = viewModel)
                }
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Total Price") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
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

                ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(it),
                ) {
                    PriceTable(viewModel = viewModel)
                }
            }
        }
    }
}