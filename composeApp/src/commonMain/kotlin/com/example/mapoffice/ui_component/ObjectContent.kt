package com.example.mapoffice.ui_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.example.mapoffice.data.MapObjectDto
import com.example.mapoffice.utils.getString
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.desktop
import mapoffice.composeapp.generated.resources.tv_outline
import org.jetbrains.compose.resources.painterResource

@Composable
fun ObjectContent(obj: MapObjectDto, scale: Float) {
    if (scale > 1.2f) {
        when (obj.objectType) {
            "TABLE" -> Column {
                Icon(painter = painterResource(Res.drawable.tv_outline), contentDescription = null)
                Text(
                    obj.properties.getString("userName"),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    obj.properties.getString("seatNumber"),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            "LABEL" -> {
                val isVertical = obj.isRotated
                val text = obj.properties.getString("text")

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        softWrap = false,
                        modifier = Modifier
                            .then(
                                if (isVertical) {
                                    Modifier.layout { measurable, constraints ->
                                        // GIẢI PHÁP: Gỡ bỏ giới hạn chiều rộng/chiều cao tối đa
                                        // để Text có thể dài tùy ý trước khi xoay
                                        val looseConstraints = constraints.copy(
                                            maxWidth = Int.MAX_VALUE,
                                            maxHeight = Int.MAX_VALUE
                                        )
                                        val placeable = measurable.measure(looseConstraints)

                                        // Tráo đổi kích thước vật lý của khung chứa sau khi xoay
                                        layout(placeable.height, placeable.width) {
                                            placeable.placeWithLayer(
                                                x = -(placeable.width - placeable.height) / 2,
                                                y = -(placeable.height - placeable.width) / 2
                                            ) {
                                                rotationZ = -90f
                                            }
                                        }
                                    }
                                } else Modifier
                            )
                    )
                }
            }

            else -> Text(
                obj.properties.getString("name"),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}