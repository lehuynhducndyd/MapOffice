package com.example.mapoffice.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mapoffice.ui_component.LabeledInfo
import kotlinx.datetime.Month

@Composable
fun AddRoomPopUp(
    onAddNewRoom: (String, Int, Int) -> Unit,
    onDimiss: () -> Unit
) {
    var name by remember { mutableStateOf("untitled") }
    var width by remember { mutableStateOf("0") }
    var height by remember { mutableStateOf("0") }
    LazyColumn(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.small,
                clip = false
            )
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(20.dp),
    ) {
        item {
            Text("Add room", fontWeight = FontWeight.Bold)
            Text("Name:")
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Text("Width (cm):")
            BasicTextField(
                value = width,
                onValueChange = { width = it },
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
            Text("Height (cm):")
            BasicTextField(
                value = height,
                onValueChange = { height = it },
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
            Row {
                Button(
                    onClick = {
                        if (name.isEmpty() || width.isEmpty() || height.isEmpty()) return@Button
                        onAddNewRoom(name, width.toInt(), height.toInt())
                    },
                    modifier = Modifier
                        .padding(8.dp).width(150.dp)
                ) {
                    Text("Save")
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
                    Text("Close")
                }
            }
            if (name.isEmpty() || width.isEmpty() || height.isEmpty()) {
                Text("Khong duoc de trong", color = Color.Red)
            }
        }
    }
}