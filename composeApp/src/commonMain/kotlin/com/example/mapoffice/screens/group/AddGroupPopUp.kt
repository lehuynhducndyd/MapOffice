package com.example.mapoffice.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mapoffice.ui_component.LabeledInfo
import com.example.mapoffice.utils.toColor
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import mapoffice.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddGroupPopUp(
    onAddNewGroup: (String, String, String) -> Unit,
    onDimiss: () -> Unit
) {
    var name by remember { mutableStateOf("title") }
    var colorCode by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
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
            Text("Add group", fontWeight = FontWeight.Bold)
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
            Text("Color: $colorCode")

            val controller = rememberColorPickerController()
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(5.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    colorCode = colorEnvelope.hexCode
                }
            )
            Box(
                modifier = Modifier.background(colorCode.toColor() ?: Color.White).size(50.dp)
                    .clip(MaterialTheme.shapes.small),
            )
            Text("Description:")
            BasicTextField(
                value = desc,
                onValueChange = { desc = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
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
            Row {
                Button(
                    onClick =
                        {
                            if (name.isEmpty() || colorCode.isEmpty()) return@Button
                            onAddNewGroup(name, colorCode, desc)
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
            if (name.isEmpty() || colorCode.isEmpty()) {
                Text("Khong duoc de trong", color = Color.Red)
            }
        }
    }
}