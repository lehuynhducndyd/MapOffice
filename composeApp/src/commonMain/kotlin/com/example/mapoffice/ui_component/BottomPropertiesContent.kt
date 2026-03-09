package com.example.mapoffice.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mapoffice.screens.canvas.Direction
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.utils.getString
import com.example.mapoffice.utils.toColor
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources._23
import mapoffice.composeapp.generated.resources.arrow_range
import mapoffice.composeapp.generated.resources.block
import mapoffice.composeapp.generated.resources.close
import mapoffice.composeapp.generated.resources.content_copy
import mapoffice.composeapp.generated.resources.desktop
import mapoffice.composeapp.generated.resources.gesture_select
import mapoffice.composeapp.generated.resources.height
import mapoffice.composeapp.generated.resources.ic_oui_arrow_down
import mapoffice.composeapp.generated.resources.ic_oui_arrow_left
import mapoffice.composeapp.generated.resources.ic_oui_arrow_right
import mapoffice.composeapp.generated.resources.ic_oui_arrow_up
import mapoffice.composeapp.generated.resources.ic_oui_close
import mapoffice.composeapp.generated.resources.ic_oui_copy_outline
import mapoffice.composeapp.generated.resources.ic_oui_rotate_outline
import mapoffice.composeapp.generated.resources.ic_oui_text_style_default
import mapoffice.composeapp.generated.resources.keyboard_arrow_down
import mapoffice.composeapp.generated.resources.keyboard_arrow_left
import mapoffice.composeapp.generated.resources.keyboard_arrow_right
import mapoffice.composeapp.generated.resources.keyboard_arrow_up
import mapoffice.composeapp.generated.resources.label
import mapoffice.composeapp.generated.resources.other_device
import mapoffice.composeapp.generated.resources.other_device_outline
import mapoffice.composeapp.generated.resources.rotate_90_degrees
import mapoffice.composeapp.generated.resources.tv_outline
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun BottomPropertiesContent(
    viewModel: EditorViewModel,
    onOpenNumber: () -> Unit,
    onOpenDelete: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val currentObj = state.mapObjects.find { it.id == state.selectedObjectId }
    LazyRow(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
    ) {

        item {
            Column {
                Row(
                    modifier = Modifier.width(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            viewModel.moveSelectedObject(
                                dx = 0f,
                                dy = -1f,
                                worldW = state.currentRoom?.width?.toFloat() ?: 0f,
                                worldH = state.currentRoom?.height?.toFloat() ?: 0f
                            )
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_up),
                            contentDescription = "up"
                        )
                    }
                }
                Row(
                    modifier = Modifier.width(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            viewModel.moveSelectedObject(
                                dx = -1f,
                                dy = 0f,
                                worldW = state.currentRoom?.width?.toFloat() ?: 0f,
                                worldH = state.currentRoom?.height?.toFloat() ?: 0f
                            )
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_left),
                            contentDescription = "left"
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.moveSelectedObject(
                                dx = 0f,
                                dy = 1f,
                                worldW = state.currentRoom?.width?.toFloat() ?: 0f,
                                worldH = state.currentRoom?.height?.toFloat() ?: 0f
                            )
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_down),
                            contentDescription = "down"
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.moveSelectedObject(
                                dx = 1f,
                                dy = 0f,
                                worldW = state.currentRoom?.width?.toFloat() ?: 0f,
                                worldH = state.currentRoom?.height?.toFloat() ?: 0f
                            )
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_right),
                            contentDescription = "right"
                        )
                    }
                }
            }

        }
        item {
            VerticalDivider(thickness = 1.dp)
        }
        item {
            Column {
                IconButton(
                    onClick = {
                        viewModel.rotateSelectedObject()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    enabled = state.selectedObjectId != 0
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_oui_rotate_outline),
                        contentDescription = "rotate"
                    )
                }
                IconButton(
                    onClick = onOpenDelete,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    enabled = state.selectedObjectId != 0
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_oui_close),
                        contentDescription = "delete"
                    )
                }
            }
        }
        item {
            VerticalDivider(thickness = 1.dp)
        }
        item {
            Row {

                IconButton(
                    onClick = {
                        viewModel.addObject("TABLE")
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        painterResource(Res.drawable.tv_outline),
                        contentDescription = "add table"
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.addObject("LABEL")
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_oui_text_style_default),
                        contentDescription = "add label"
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.addObject("OBJECT")
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        painterResource(Res.drawable.other_device_outline),
                        contentDescription = "add object"
                    )
                }
            }

        }
        item {
            Column {
                Row(
                    modifier = Modifier.width(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            viewModel.duplicate(Direction.UP)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        enabled = state.selectedObjectId != 0,
                    ) {
                        PairIcon(Res.drawable.ic_oui_copy_outline, Res.drawable.keyboard_arrow_up)
                    }
                }
                Row(
                    modifier = Modifier.width(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            viewModel.duplicate(Direction.LEFT)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        enabled = state.selectedObjectId != 0,
                    ) {
                        PairIcon(Res.drawable.ic_oui_copy_outline, Res.drawable.keyboard_arrow_left)
                    }
                    IconButton(
                        onClick = {
                            viewModel.duplicate(Direction.DOWN)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        enabled = state.selectedObjectId != 0,
                    ) {
                        PairIcon(Res.drawable.ic_oui_copy_outline, Res.drawable.keyboard_arrow_down)
                    }

                    IconButton(
                        onClick = {
                            viewModel.duplicate(Direction.RIGHT)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        enabled = state.selectedObjectId != 0,
                    ) {
                        PairIcon(
                            Res.drawable.ic_oui_copy_outline,
                            Res.drawable.keyboard_arrow_right
                        )
                    }
                }
            }

        }
        item {
            VerticalDivider(thickness = 1.dp)
        }


        item {
            Column {
                IconButton(
                    onClick = {
                        viewModel.setIsSelectVertical(true)
                        viewModel.setSelectedObjectIdList()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    enabled = state.selectedObjectId != 0
                ) {
                    PairIcon(Res.drawable.gesture_select, Res.drawable.height)

                }
                IconButton(
                    onClick = {
                        viewModel.setIsSelectVertical(false)
                        viewModel.setSelectedObjectIdList()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    enabled = state.selectedObjectId != 0
                ) {
                    PairIcon(Res.drawable.gesture_select, Res.drawable.arrow_range)

                }
            }
            Column {
                IconButton(
                    onClick = onOpenNumber,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    enabled = state.selectedObjectIdList.isNotEmpty()
                ) {
                    Icon(
                        painterResource(Res.drawable._23),
                        contentDescription = "numbered"
                    )
                }
            }
        }
    }
}

@Composable
fun PairIcon(icon1: DrawableResource, icon2: DrawableResource) {
    Box(
        modifier = Modifier.size(35.dp)
    ) {
        Icon(
            painterResource(icon1),
            contentDescription = "select horizontal",
            modifier = Modifier.size(20.dp).align(Alignment.BottomStart)
        )
        Icon(
            painterResource(icon2),
            contentDescription = "select horizontal",
            modifier = Modifier.size(20.dp).align(Alignment.TopEnd)

        )

    }
}
