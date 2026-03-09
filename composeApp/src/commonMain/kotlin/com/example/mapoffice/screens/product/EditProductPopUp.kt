package com.example.mapoffice.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mapoffice.data.ProductDTO


@Composable
fun EditProductPopUp(
    currentProduct: ProductDTO,
    onEditNewProduct: (Int, String, String, Float) -> Unit,
    onDimiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentProduct.name) }
    var type by remember { mutableStateOf(currentProduct.type) }
    var price by remember { mutableStateOf(currentProduct.price.toString()) }

    val select = if (type == "OBJECT") 0 else 1
    val options = listOf("Object", "Table")
    var selectedIndex by remember { mutableIntStateOf(select) }
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
            Text("Edit object", fontWeight = FontWeight.Bold)
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
            Text("Price:")
            BasicTextField(
                value = price,
                onValueChange = { price = it },
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
            Row {
                Button(
                    onClick = {
                        if (selectedIndex == 0) {
                            type = "OBJECT"
                        } else {
                            type = "TABLE"
                        }
                        if (name.isEmpty() || price.isEmpty()) return@Button
                        onEditNewProduct(currentProduct.id, name, type, price.toFloat())
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
            if (name.isEmpty() || price.isEmpty()) {
                Text("Khong duoc de trong", color = Color.Red)
            }
        }
    }
}