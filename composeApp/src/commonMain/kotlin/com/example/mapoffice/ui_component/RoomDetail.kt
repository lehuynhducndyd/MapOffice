package com.example.mapoffice.ui_component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.screens.main.MainViewModel
import com.example.mapoffice.utils.getString
import com.example.mapoffice.utils.toColor

@Composable
fun RoomDetail(modifier: Modifier, viewModel: EditorViewModel) {
    val state by viewModel.uiState.collectAsState()
    Column(modifier = modifier) {
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
                    LabeledInfo("Name", state.currentRoom?.name ?: "")
                    LabeledInfo("Width", state.currentRoom?.width.toString())
                    LabeledInfo("Height", state.currentRoom?.height.toString())
                }
            }
            item {
                Text(
                    text = "List Object",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                state.groups.forEach { group ->

                    val objList = state.mapObjects.filter { it.groupId == group.id }
                    if (!objList.isEmpty()) {
                        Text(group.name + ":", fontWeight = FontWeight.Bold)
                        state.mapObjects.filter { it.groupId == group.id }.forEach { obj ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                onClick = {
                                    viewModel.setSelectedObjectId(obj.id)
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                border = if (state.selectedObjectId == obj.id) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    null
                                },
                            ) {

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    LabeledInfo("Object", obj.objectType)
                                    Spacer(Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier.background(
                                            obj.userGroup?.getString("colorCode")?.toColor()
                                                ?: Color.LightGray
                                        ).size(30.dp)
                                    )
                                }
                                if (obj.objectType == "TABLE") {
                                    LabeledInfo("Username", obj.properties.getString("userName"))
                                    LabeledInfo(
                                        "Seat Number",
                                        obj.properties.getString("seatNumber")
                                    )
                                    LabeledInfo("Product", obj.product.getString("name"))
                                    LabeledInfo("Table", obj.productTable.getString("name"))
                                } else if (obj.objectType == "LABEL") {
                                    LabeledInfo("Text", obj.properties.getString("text"))
                                } else {
                                    LabeledInfo("Name", obj.properties.getString("name"))
                                    LabeledInfo("Product", obj.product.getString("name"))
                                }
                            }
                        }
                    }
                }
                Text("Table no-group:", fontWeight = FontWeight.Bold)
                state.mapObjects.filter { it.groupId == null && it.objectType == "TABLE" }
                    .forEach { obj ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            onClick = {
                                viewModel.setSelectedObjectId(obj.id)
                            },
                            colors = if (obj.objectType == "TABLE") CardDefaults.cardColors(
                                containerColor = obj.userGroup?.getString("colorCode")?.toColor()
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            ) else if (obj.objectType == "OBJECT") CardDefaults.cardColors(
                                containerColor = obj.userGroup?.getString("colorCode")?.toColor()
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            ) else CardDefaults.cardColors(),
                            border = if (state.selectedObjectId == obj.id) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                null
                            },
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                LabeledInfo("Object", obj.objectType)
                                Spacer(Modifier.weight(1f))
                                Box(
                                    modifier = Modifier.background(
                                        obj.userGroup?.getString("colorCode")?.toColor()
                                            ?: Color.LightGray
                                    ).size(30.dp)
                                )
                            }
                            if (obj.objectType == "TABLE") {
                                LabeledInfo("Username", obj.properties.getString("userName"))
                                LabeledInfo("Seat Number", obj.properties.getString("seatNumber"))
                                LabeledInfo("Product", obj.product.getString("name"))
                            } else if (obj.objectType == "LABEL") {
                                LabeledInfo("Text", obj.properties.getString("text"))
                            } else {
                                LabeledInfo("Name", obj.properties.getString("name"))
                                LabeledInfo("Product", obj.product.getString("name"))
                            }
                        }
                    }
                Text("Others:", fontWeight = FontWeight.Bold)
                state.mapObjects.filter { it.groupId == null && it.objectType != "TABLE" }
                    .forEach { obj ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            onClick = {
                                viewModel.setSelectedObjectId(obj.id)
                            },
                            colors = if (obj.objectType == "TABLE") CardDefaults.cardColors(
                                containerColor = obj.userGroup?.getString("colorCode")?.toColor()
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            ) else if (obj.objectType == "OBJECT") CardDefaults.cardColors(
                                containerColor = obj.userGroup?.getString("colorCode")?.toColor()
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            ) else CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (state.selectedObjectId == obj.id) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                null
                            },
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                LabeledInfo("Object", obj.objectType)
                                Spacer(Modifier.weight(1f))
                                Box(
                                    modifier = Modifier.background(
                                        obj.userGroup?.getString("colorCode")?.toColor()
                                            ?: Color.LightGray
                                    ).size(30.dp)
                                )
                            }
                            if (obj.objectType == "TABLE") {
                                LabeledInfo("Username", obj.properties.getString("userName"))
                                LabeledInfo("Seat Number", obj.properties.getString("seatNumber"))
                                LabeledInfo("Product", obj.product.getString("name"))
                            } else if (obj.objectType == "LABEL") {
                                LabeledInfo("Text", obj.properties.getString("text"))
                            } else {
                                LabeledInfo("Name", obj.properties.getString("name"))
                                LabeledInfo("Product", obj.product.getString("name"))
                            }
                        }
                    }
            }
            item {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }

}