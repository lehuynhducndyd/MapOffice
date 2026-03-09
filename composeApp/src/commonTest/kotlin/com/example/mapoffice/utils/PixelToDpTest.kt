// composeApp/src/commonTest/kotlin/com/example/mapoffice/utils/PixelUtilsTest.kt
package com.example.mapoffice.utils // Sửa package theo project của bạn

import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class PixelUtilsTest {

    @Test
    fun testPixelToDp_QuyDoiChinhXac() {
        // Chỉ cần truyền pixel và density giả (2.0f) vào
        val result = pixelToDpCore(pixel = 100, density = 2f)

        // Nhanh, gọn, lẹ và KHÔNG BAO GIỜ bị lỗi FINGERPRINT
        assertThat(result).isEqualTo(50.dp)
    }
}