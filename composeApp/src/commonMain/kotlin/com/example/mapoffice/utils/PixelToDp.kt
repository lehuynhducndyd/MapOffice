package com.example.mapoffice.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


// Tách phần tính toán ra một hàm thuần túy (Không có @Composable)
fun pixelToDpCore(pixel: Int, density: Float): Dp {
    return (pixel / density).dp
}

// Hàm Composable chỉ làm nhiệm vụ gọi lại hàm kia
@Composable
fun pixelToDp(pixel: Int): Dp {
    return pixelToDpCore(pixel, LocalDensity.current.density)
}