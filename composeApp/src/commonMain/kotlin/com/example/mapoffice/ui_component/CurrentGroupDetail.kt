package com.example.mapoffice.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapoffice.screens.group.GroupScreenViewModel
import com.example.mapoffice.utils.getString
import com.example.mapoffice.utils.toColor

@Composable
fun CurrentGroupDetail(modifier: Modifier, viewModel: GroupScreenViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getGroups()
    }
    val state by viewModel.uiState.collectAsState()
    Column(modifier = modifier) {
        if (state.currentGroup == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No group is selected")
            }
            return@Column
        }
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Table", "Object")


        LazyColumn {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = {
                        //
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {

                    LabeledInfo("Name", state.currentGroup?.name ?: "")
                    LabeledInfo("Description", state.currentGroup?.description ?: "")
                    LabeledInfo("Total", state.currentGroup?.mapObjects?.size.toString())
                    Row {
                        LabeledInfo("Color", "")
                        Box(
                            modifier = Modifier.background(
                                state.currentGroup!!.colorCode?.toColor() ?: Color.Gray
                            ).size(30.dp)
                        )
                    }

                }
            }
            item {
                SingleChoiceSegmentedButtonRow {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
                            label = { Text(label) },
                            colors = SegmentedButtonDefaults.colors(
                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    }
                }
            }
            if (selectedIndex == 0) {
                item {
                    Text(
                        text = "List Table",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LabeledInfo(
                        "All table",
                        state.currentGroup?.mapObjects?.count { it.objectType == "TABLE" }
                            .toString()
                    )
                    state.currentGroup?.mapObjects?.filter { it.objectType == "TABLE" }
                        ?.forEach { it ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                onClick = {
                                    //
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                             
                                LabeledInfo("Room", viewModel.getRoomName(it.roomId))
                                LabeledInfo("Username", it.properties.getString("userName"))
                                LabeledInfo(
                                    "Seat Number",
                                    it.properties.getString("seatNumber")
                                )
                                LabeledInfo("Obj", it.product.getString("name"))
                                LabeledInfo("Table", it.productTable.getString("name"))
                            }
                        }

                }
            } else {
                item {
                    Text(
                        text = "List Object",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LabeledInfo(
                        "All object",
                        state.currentGroup?.mapObjects?.count { it.objectType == "OBJECT" }
                            .toString()
                    )
                    state.currentGroup?.mapObjects?.filter { it.objectType == "OBJECT" }
                        ?.forEach { it ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                onClick = {
                                    //
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Row {
                                    LabeledInfo("ID", it.id.toString())
                                    LabeledInfo("Object", it.objectType)
                                }
                                LabeledInfo("Room", viewModel.getRoomName(it.roomId))
                                LabeledInfo("Name", it.properties.getString("name"))

                            }
                        }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

}