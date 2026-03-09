package com.example.mapoffice.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.screens.main.MainViewModel
import com.example.mapoffice.screens.product.ProductScreenViewModel
import com.example.mapoffice.utils.getString
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.write
import io.github.windedge.table.DataTable
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PriceTable(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {

    val state by viewModel.uiState.collectAsState()

    val objList = state.currentRoom?.mapObjects


    val groupedObjects = objList?.filter { it.productId != null }
        ?.groupBy { it.productId } ?: emptyMap()
    val groupedTable = objList?.filter { it.productTableId != null }
        ?.groupBy { it.productTableId } ?: emptyMap()
    var data = groupedObjects.values.map { groupItems ->
        val product = groupItems.first().product
        mapOf(
            "Tên" to (product?.getString("name") ?: "N/A"),
            "Đơn giá" to (product?.getString("price") ?: "0"),
            // Kích thước của nhóm (size) chính là SỐ LƯỢNG bạn cần!
            "Số lượng" to groupItems.size.toString(),
            "Tổng tiền" to (groupItems.size * (product?.getString("price")?.toInt()
                ?: 0)).toString()
        )
    }
    val data2 = groupedTable.values.map { groupItems ->
        val product = groupItems.first().productTable
        mapOf(
            "Tên" to (product?.getString("name") ?: "N/A"),
            "Đơn giá" to (product?.getString("price") ?: "0"),
            // Kích thước của nhóm (size) chính là SỐ LƯỢNG bạn cần!
            "Số lượng" to groupItems.size.toString(),
            "Tổng tiền" to (groupItems.size * (product?.getString("price")?.toInt()
                ?: 0)).toString()
        )
    }
    val sum =
        data.sumOf { it["Tổng tiền"]?.toInt() ?: 0 } + data2.sumOf { it["Tong tien"]?.toInt() ?: 0 }
    data = data + data2

    LazyColumn {
        item {
            DataTable(
                columns = {
                    headerBackground {
                        Box(Modifier.background(Color.LightGray))
                    }
                    column { Text("Tên", overflow = TextOverflow.Ellipsis) }
                    column { Text("Đơn giá", overflow = TextOverflow.Ellipsis) }
                    column { Text("Số lượng", overflow = TextOverflow.Ellipsis) }
                    column { Text("Tổng tiền", overflow = TextOverflow.Ellipsis) }

                },
            ) {
                data.forEach { item ->
                    row(modifier = Modifier) {
                        cell(
                            modifier = Modifier.height(70.dp)
                        ) { Text(item["Tên"] ?: "", overflow = TextOverflow.Ellipsis) }
                        cell { Text(item["Đơn giá"] ?: "", overflow = TextOverflow.Ellipsis) }
                        cell { Text(item["Số lượng"] ?: "", overflow = TextOverflow.Ellipsis) }
                        cell { Text(item["Tổng tiền"] ?: "", overflow = TextOverflow.Ellipsis) }
                    }
                }
            }
        }

        item {
            Column(Modifier.padding(8.dp)) {
                Text("Total: $sum")
                ExportCsvKmpButton(data)
            }


        }
    }
}

@Composable
fun ExportCsvKmpButton(products: List<Map<String, String>>) {

    val csvContent = buildString {
        if (products.isNotEmpty()) {
            appendLine(products.first().keys.joinToString(","))
        }
        products.forEach { product ->
            appendLine(product.values.joinToString(",") { value ->
                if (value.contains(",") || value.contains("\n")) "\"$value\""
                else value
            })
        }
    }
    val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
    val bytes = bom + csvContent.encodeToByteArray()

    val scope = rememberCoroutineScope()
    val launcher = rememberFileSaverLauncher(
        dialogSettings = FileKitDialogSettings.createDefault()
    ) { file ->
        // Write your data to the file
        if (file != null) {
            scope.launch {
                file.write(bytes)
            }
        }
    }
    Button(onClick = { launcher.launch("document", "csv") }) {
        Text("Export CSV")
    }
}