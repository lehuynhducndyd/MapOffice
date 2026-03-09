package com.example.mapoffice.ui_component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapoffice.screens.main.MainViewModel


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RoomList(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val windowSizeClass = calculateWindowSizeClass()
    Column(modifier = modifier) {
        LazyColumn {
            items(state.rooms.size) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    onClick = {
                        viewModel.getRoom(state.rooms[it].id)
                        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                            navController.navigate("room/detail/${state.rooms[it].id}")
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.medium,

                    border = if (state.currentRoom?.id == state.rooms[it].id)
                        BorderStroke(1.dp, Color.Gray) else BorderStroke(
                        0.dp,
                        Color.Transparent
                    )
                )
                {
                    //Text("Name: ${state.rooms[it].name}")
                    LabeledInfo("Name", state.rooms[it].name)
                    LabeledInfo("Width", state.rooms[it].width.toString())
                    LabeledInfo("Height", state.rooms[it].height.toString())
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

@Composable
fun LabeledInfo(
    label: String,
    data: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(vertical = 2.dp, horizontal = 24.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold)
        Text(data)
    }
}
