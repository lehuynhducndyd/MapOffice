package com.example.mapoffice.ui_component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapoffice.screens.group.GroupScreenViewModel
import com.example.mapoffice.utils.toColor
import kotlinx.datetime.Month


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun GroupList(modifier: Modifier, viewModel: GroupScreenViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    val windowSizeClass = calculateWindowSizeClass()
    Column(modifier = modifier) {
        LazyColumn {
            items(state.groups.size) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    onClick = {
                        viewModel.getGroup(state.groups[it].id)
                        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                            navController.navigate("group/detail/${state.groups[it].id}")
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.medium,
                    border = if (state.currentGroup?.id == state.groups[it].id)
                        BorderStroke(1.dp, Color.Gray) else BorderStroke(0.dp, Color.Transparent)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(state.groups[it].name)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Box(
                            modifier = Modifier.background(
                                state.groups[it].colorCode?.toColor() ?: Color.Gray
                            ).size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                }
            }
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                item {
                    Spacer(modifier = Modifier.height(360.dp))
                }
            }
        }
    }

}
