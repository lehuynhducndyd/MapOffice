package com.example.mapoffice.ui_component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapoffice.data.ProductDTO
import com.example.mapoffice.screens.main.MainViewModel
import com.example.mapoffice.screens.product.ProductScreenViewModel
import kotlin.collections.find


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ProductList(
    modifier: Modifier = Modifier,
    viewModel: ProductScreenViewModel,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    var listProducts = state.products
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Object", "Table")
    Column(modifier = modifier) {
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
        if (selectedIndex == 0) {
            List(
                viewModel = viewModel,
                listProducts = listProducts.filter { it.type == "OBJECT" }
            )
        } else {
            List(
                viewModel = viewModel,
                listProducts = listProducts.filter { it.type == "TABLE" }
            )
        }

    }

}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun List(
    modifier: Modifier = Modifier,
    viewModel: ProductScreenViewModel,
    listProducts: List<ProductDTO>,
) {
    val state by viewModel.uiState.collectAsState()
    val windowSizeClass = calculateWindowSizeClass()
    val numberOfColumns: Int = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }
    LazyVerticalGrid(
        modifier = Modifier.padding(8.dp),
        columns = GridCells.Fixed(numberOfColumns),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listProducts.size) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.getProduct(listProducts[it].id)
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium,

                border = if (state.currentProduct?.id == listProducts[it].id)
                    BorderStroke(1.dp, Color.Gray) else BorderStroke(
                    0.dp,
                    Color.Transparent
                )
            )
            {
                var inUse = 0
                inUse = if (listProducts[it].type == "TABLE") {
                    state.objects.count { obj -> obj.productTableId == listProducts[it].id }
                } else {
                    state.objects.count { obj -> obj.productId == listProducts[it].id }
                }
                //Text("Name: ${state.rooms[it].name}")
                LabeledInfo("Name", listProducts[it].name)
                //LabeledInfo("Desc", listProducts[it].type ?: "")
                LabeledInfo("Price", listProducts[it].price.toLong().toString())
                LabeledInfo("InUse", inUse.toString())
            }
        }
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            item {
                Spacer(modifier = Modifier.height(360.dp))
            }
        }
    }
}
