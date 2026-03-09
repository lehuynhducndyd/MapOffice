package com.example.mapoffice.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mapoffice.data.MapObjectDto
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.data.UpdateMapObjectDTO
import com.example.mapoffice.data.UserGroupDto
import com.example.mapoffice.screens.canvas.EditorState
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.utils.getString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.close
import mapoffice.composeapp.generated.resources.ic_oui_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun DetailObject(state: EditorState, viewModel: EditorViewModel, isOpen: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val currentObj = state.mapObjects.find { it.id == state.selectedObjectId }
    if (currentObj == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = isOpen,
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_oui_close),
                            contentDescription = "close"
                        )
                    }
                }
                Text("No object selected")
            }
        }

        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = isOpen,
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_oui_close),
                        contentDescription = "close"
                    )
                }
            }

            LabeledInfo("Type", currentObj.objectType)
            when (currentObj.objectType) {
                "TABLE" -> TableForm(currentObj, state.groups, state.products, viewModel)
                "LABEL" -> LabelForm(currentObj, state.groups, viewModel)
                else -> ObjectForm(currentObj, state.groups, state.products, viewModel)

            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectForm(
    obj: MapObjectDto,
    groups: List<UserGroupDto>,
    products: List<ProductDTO>,
    viewModel: EditorViewModel
) {
    var width by remember(obj.id) { mutableStateOf(obj.width.toString()) }
    var height by remember(obj.id) { mutableStateOf(obj.height.toString()) }
    var name by remember(obj.id) { mutableStateOf(obj.properties.getString("name")) }


    LabeledInfo("Width", "")
    BasicTextField(
        value = width,
        onValueChange = { width = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Height", "")
    BasicTextField(
        value = height,
        onValueChange = { height = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Name", "")
    BasicTextField(
        value = name,
        onValueChange = { name = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )

    var selectedOption by remember { mutableStateOf(obj.groupId) }
    var selectedOption2 by remember { mutableStateOf(obj.productId) }
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    LabeledInfo("Group name", "")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = groups.find { it.id == selectedOption }?.name ?: "None",
            onValueChange = {},
            readOnly = true, // Không cho gõ phím, chỉ cho chọn
            label = { Text("Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedOption = null
                    expanded = false
                }
            )
            groups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        selectedOption = group.id
                        expanded = false
                        //viewmodel
                    }
                )
            }
        }
    }
    LabeledInfo("Product name", "")
    ExposedDropdownMenuBox(
        expanded = expanded2,
        onExpandedChange = { expanded2 = !expanded2 }
    ) {
        OutlinedTextField(
            value = products.find { it.id == selectedOption2 }?.name ?: "None",
            onValueChange = {},
            readOnly = true, // Không cho gõ phím, chỉ cho chọn
            label = { Text("Product") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded2,
            onDismissRequest = { expanded2 = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedOption2 = null
                    expanded2 = false
                }
            )
            products.filter { it.type == "OBJECT" }.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        selectedOption2 = product.id
                        expanded2 = false
                        //viewmodel
                    }
                )
            }
        }
    }
    Button(
        onClick = {
            viewModel.updateLocalObject(
                obj.id,
                UpdateMapObjectDTO(
                    posX = obj.posX,
                    posY = obj.posY,
                    width = width.toInt(),
                    height = height.toInt(),
                    groupId = selectedOption?.toInt(),
                    productId = selectedOption2?.toInt(),
                    productTableId = null,
                    properties = buildJsonObject {
                        put("name", name)
                    },
                    isRotated = obj.isRotated
                )
            )
        },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text("Save")
    }
}

@Composable
fun LabelForm(obj: MapObjectDto, groups: List<UserGroupDto>, viewModel: EditorViewModel) {
    var width by remember(obj.id) { mutableStateOf(obj.width.toString()) }
    var height by remember(obj.id) { mutableStateOf(obj.height.toString()) }
    var text by remember(obj.id) { mutableStateOf(obj.properties.getString("text")) }

    LabeledInfo("Width", "")
    BasicTextField(
        value = width,
        onValueChange = { width = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Height", "")
    BasicTextField(
        value = height,
        onValueChange = { height = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Text", "")
    BasicTextField(
        value = text,
        onValueChange = { text = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )


    Button(
        onClick = {
            viewModel.updateLocalObject(
                obj.id,
                UpdateMapObjectDTO(
                    posX = obj.posX,
                    posY = obj.posY,
                    width = width.toInt(),
                    height = height.toInt(),
                    groupId = null,
                    productId = null,
                    productTableId = null,
                    properties = buildJsonObject {
                        put("text", text)
                    },
                    isRotated = obj.isRotated
                )
            )
        },
        modifier = Modifier
            .padding(8.dp)

    ) {
        Text("Save")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableForm(
    obj: MapObjectDto,
    groups: List<UserGroupDto>,
    products: List<ProductDTO>,
    viewModel: EditorViewModel
) {

    var width by remember(obj.id) { mutableStateOf(obj.width.toString()) }
    var height by remember(obj.id) { mutableStateOf(obj.height.toString()) }
    var userName by remember(obj.id) { mutableStateOf(obj.properties.getString("userName")) }
    var seatNumber by remember(obj.id) { mutableStateOf(obj.properties.getString("seatNumber")) }


    LabeledInfo("Width", "")
    BasicTextField(
        value = width,
        onValueChange = { width = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Height", "")
    BasicTextField(
        value = height,
        onValueChange = { height = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("Username", "")
    BasicTextField(
        value = userName,
        onValueChange = { userName = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )
    LabeledInfo("SeatNumber", "")
    BasicTextField(
        value = seatNumber,
        onValueChange = { seatNumber = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(4.dp)
    )

    var selectedOption by remember(obj.id) { mutableStateOf(obj.groupId) }
    var selectedOption2 by remember(obj.id) { mutableStateOf(obj.productId) }
    var selectedOption3 by remember(obj.id) { mutableStateOf(obj.productTableId) }
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }
    LabeledInfo("Group name", "")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = groups.find { it.id == selectedOption }?.name ?: "None",
            onValueChange = {},
            readOnly = true, // Không cho gõ phím, chỉ cho chọn
            label = { Text("Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedOption = null
                    expanded = false
                }
            )
            groups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        selectedOption = group.id
                        expanded = false
                        //viewmodel
                    }
                )
            }
        }
    }
    LabeledInfo("Product name", "")
    ExposedDropdownMenuBox(
        expanded = expanded2,
        onExpandedChange = { expanded2 = !expanded2 }
    ) {
        OutlinedTextField(
            value = products.find { it.id == selectedOption2 }?.name ?: "None",
            onValueChange = {},
            readOnly = true, // Không cho gõ phím, chỉ cho chọn
            label = { Text("Product") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded2,
            onDismissRequest = { expanded2 = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedOption2 = null
                    expanded2 = false
                }
            )
            products.filter { it.type == "OBJECT" }.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        selectedOption2 = product.id
                        expanded2 = false
                        //viewmodel
                    }
                )
            }
        }
    }
    LabeledInfo("Table type", "")
    ExposedDropdownMenuBox(
        expanded = expanded3,
        onExpandedChange = { expanded3 = !expanded3 }
    ) {
        OutlinedTextField(
            value = products.find { it.id == selectedOption3 }?.name ?: "None",
            onValueChange = {},
            readOnly = true, // Không cho gõ phím, chỉ cho chọn
            label = { Text("Table") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded3) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded3,
            onDismissRequest = { expanded3 = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedOption3 = null
                    expanded3 = false
                }
            )
            products.filter { it.type == "TABLE" }.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        selectedOption3 = product.id
                        expanded3 = false
                        //viewmodel
                    }
                )
            }
        }
    }
    Button(
        onClick = {
            viewModel.updateLocalObject(
                obj.id,
                UpdateMapObjectDTO(
                    posX = obj.posX,
                    posY = obj.posY,
                    width = width.toInt(),
                    height = height.toInt(),
                    groupId = selectedOption?.toInt(),
                    productId = selectedOption2?.toInt(),
                    productTableId = selectedOption3?.toInt(),
                    properties = buildJsonObject {
                        put("userName", userName)
                        put("seatNumber", seatNumber)
                    },
                    isRotated = obj.isRotated
                )
            )
        },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text("Save")
    }
}