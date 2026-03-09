package com.example.mapoffice.screens.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mapoffice.theme.XlBlue
import com.example.mapoffice.ui_component.LabeledInfo
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.arrow_left_alt
import mapoffice.composeapp.generated.resources.arrow_right_alt
import mapoffice.composeapp.generated.resources.ic_oui_arrow_down
import mapoffice.composeapp.generated.resources.ic_oui_arrow_left
import mapoffice.composeapp.generated.resources.ic_oui_arrow_right
import mapoffice.composeapp.generated.resources.ic_oui_arrow_up
import mapoffice.composeapp.generated.resources.keyboard_arrow_down
import mapoffice.composeapp.generated.resources.keyboard_arrow_up
import org.jetbrains.compose.resources.painterResource

@Composable
fun NumberPopUp(
    viewModel: EditorViewModel,
    onDimiss: () -> Unit
) {
    var start by remember { mutableStateOf("0") }
    val state by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,                // Độ cao của bóng (càng lớn bóng càng to)
                shape = MaterialTheme.shapes.small, // Hình dáng bóng (phải khớp với shape của bạn)
                clip = false                     // Thường để false để bóng lan ra ngoài
            )
            //.background(Color(0xFFD9D9D9))
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(20.dp),
    ) {
        item {
            Text("Setting")
        }
        item {
            LabeledInfo("Start", "")
            BasicTextField(
                value = start,
                onValueChange = { start = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!state.isSelectVertical) {
                    IconButton(
                        onClick = {
                            viewModel.setIsLeftToRight(false)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (state.isLeftToRight) MaterialTheme.colorScheme.surfaceVariant else XlBlue,
                                shape = MaterialTheme.shapes.medium
                            ),
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_left),
                            contentDescription = "Left to right"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setIsLeftToRight(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (!state.isLeftToRight) MaterialTheme.colorScheme.surfaceVariant else XlBlue,
                                shape = MaterialTheme.shapes.medium
                            ),
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_right),
                            contentDescription = "Right to left"
                        )
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.setIsTopToBottom(false) },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (state.isTopToBottom) MaterialTheme.colorScheme.surfaceVariant else XlBlue,
                                shape = MaterialTheme.shapes.medium
                            ),
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_up),
                            contentDescription = "Bot to top"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setIsTopToBottom(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (!state.isTopToBottom) MaterialTheme.colorScheme.surfaceVariant else XlBlue,
                                shape = MaterialTheme.shapes.medium
                            ),
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_arrow_down),
                            contentDescription = "top to bot"
                        )
                    }
                }


            }
        }
        item {
            Row {
                Button(
                    onClick = {
                        viewModel.numberedObject(start.toInt())
                        onDimiss()
                    },
                    modifier = Modifier
                        .padding(8.dp).width(150.dp),
                ) {
                    Text("OK")
                }
                Button(
                    onClick = onDimiss,
                    modifier = Modifier
                        .padding(8.dp).width(150.dp),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}