package com.example.mapoffice.screens.canvas


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.mapoffice.data.MapObjectDto
import com.example.mapoffice.data.UpdateMapObjectDTO
import com.example.mapoffice.ui_component.ObjectContent
import com.example.mapoffice.utils.getString
import com.example.mapoffice.utils.pixelToDp
import com.example.mapoffice.utils.toColor
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.add_row_above
import mapoffice.composeapp.generated.resources.arrow_range
import mapoffice.composeapp.generated.resources.close
import mapoffice.composeapp.generated.resources.desktop
import mapoffice.composeapp.generated.resources.edit_note
import mapoffice.composeapp.generated.resources.hand_gesture
import mapoffice.composeapp.generated.resources.hand_gesture_off
import mapoffice.composeapp.generated.resources.height
import mapoffice.composeapp.generated.resources.l6_gf
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import com.example.mapoffice.theme.DarkGrey
import com.example.mapoffice.theme.XlBlue
import com.example.mapoffice.ui_component.PairIcon
import io.github.vinceglb.filekit.readBytes
import mapoffice.composeapp.generated.resources.add_photo
import mapoffice.composeapp.generated.resources.grid_3x3
import mapoffice.composeapp.generated.resources.grid_3x3_off
import mapoffice.composeapp.generated.resources.ic_oui_refresh
import mapoffice.composeapp.generated.resources.image
import mapoffice.composeapp.generated.resources.visibility
import mapoffice.composeapp.generated.resources.visibility_off


const val GRID_STEP_CM = 10f   //10cm
const val SNAP_STEP_CM = 10f    //1cm


@Composable
fun CanvasScreen(
    viewModel: EditorViewModel,
    state: EditorState,
    onDelete: () -> Unit,
    onOpenDetail: () -> Unit
) {

    var scale by remember { mutableStateOf(state.scale) }
    var offset by remember { mutableStateOf(state.offset) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val worldSizeWidth = state.currentRoom?.width?.toFloat() ?: 1000f
    val worldSizeHeight = state.currentRoom?.height?.toFloat() ?: 800f


    var objects = state.mapObjects

    LaunchedEffect(state.currentRoom?.id) { // Chỉ reset khi đổi phòng
        scale = state.scale
        offset = state.offset
    }

    var bgImagePos by remember { mutableStateOf(Offset(0f, 0f)) }
    var bgImageScale by remember { mutableStateOf(1f) }

    val onCanvasTap: () -> Unit = {
        viewModel.setSelectedObjectId(0)
        viewModel.setSelectedObjectIdList(emptyList())
    }

    val scope = rememberCoroutineScope()
    // 1. SETUP LAUNCHER (FileKit)
    var bgBitmap by remember { mutableStateOf<ImageBitmap?>(state.bgBitmap) }
    LaunchedEffect(state.bgBitmap) { // Chỉ reset khi đổi phòng
        bgBitmap = state.bgBitmap
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EAEA))
            .onSizeChanged { viewportSize = it }
            .pointerInput(state.isBgImg) {
                if (!state.isBgImg) {
                    detectTapGestures(
                        onTap = { onCanvasTap() }
                    )
                }
            }
            // XỬ LÝ ZOOM VÀ PAN CỦA TOÀN BỘ SÀN
            .pointerInput(state.isBgImg) {
                if (!state.isBgImg) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.4f, 5f)
                        val scaleChange = newScale / scale
                        offset = (offset - centroid) * scaleChange + centroid + pan
                        scale = newScale
                        val screenCenterX = viewportSize.width / 3f
                        val screenCenterY = viewportSize.height / 2.5f
                        val realCenterX = (screenCenterX - offset.x) / scale
                        val realCenterY = (screenCenterY - offset.y) / scale
                        viewModel.setCenterX(realCenterX)
                        viewModel.setCenterY(realCenterY)
                    }
                }
            }
    ) {
        // backgroud img

        // --- LAYER 0: ẢNH NỀN (DÙNG CANVAS) ---
        if (state.isShowBg && bgBitmap != null) {
            val bgPainter = remember(state.bgBitmap) {
                BitmapPainter(bgBitmap!!)
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize() // Canvas luôn phủ đầy màn hình để vẽ bất cứ đâu
                    .zIndex(0f)
                    // LOGIC CẢM ỨNG (Giữ nguyên logic tính toán của bạn)
                    .pointerInput(state.isBgImg) {
                        if (state.isBgImg) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                val newBgScale = (bgImageScale * zoom).coerceIn(0.1f, 5f)
                                val scaleChange = newBgScale / bgImageScale
                                // Chuyển đổi tâm xoay từ màn hình (Screen) về thế giới (World)
                                val worldCentroid = (centroid - offset) / scale

                                bgImagePos =
                                    (bgImagePos - worldCentroid) * scaleChange + worldCentroid + (pan / scale)
                                bgImageScale = newBgScale
                            }
                        }
                    }
            ) {
                // 1. Lấy kích thước thật của ảnh
                val srcSize = bgPainter.intrinsicSize
                if (srcSize != Size.Unspecified && srcSize.width > 0 && srcSize.height > 0) {

                    // 2. Logic ép ảnh nằm ngang (như yêu cầu cũ)
                    // Luôn lấy cạnh lớn làm Width, cạnh nhỏ làm Height
                    val baseW = srcSize.width
                    val baseH = srcSize.height

                    // 3. Tính toán kích thước vẽ (Destination Size)
                    // Kích thước cuối = Kích thước gốc * Scale riêng của ảnh * Scale chung của Camera
                    val drawW = baseW * bgImageScale * scale
                    val drawH = baseH * bgImageScale * scale

                    // 4. Tính toán vị trí vẽ (Destination Offset - TopLeft)
                    // Vị trí cuối = (Vị trí thế giới * Scale chung) + Offset Camera
                    val drawX = (bgImagePos.x * scale) + offset.x
                    val drawY = (bgImagePos.y * scale) + offset.y

                    // 5. Thực hiện vẽ
                    // Dùng hàm translate để dời bút vẽ đến vị trí cần thiết
                    withTransform({
                        translate(left = drawX, top = drawY)
                    }) {
                        // Vẽ ảnh với kích thước đã tính toán
                        with(bgPainter) {
                            draw(size = Size(drawW, drawH))
                        }
                    }

                    // 6. Vẽ viền đỏ khi đang Edit (Vẽ đè lên trên ảnh)
                    if (state.isBgImg) {
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(drawX, drawY),
                            size = Size(drawW, drawH),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }


        // --- LAYER 1: VẼ LƯỚI (GRID) ---
        // Chúng ta dùng Canvas vẽ lưới dựa trên scale và offset hiện tại
        Canvas(
            modifier = Modifier.size(
                pixelToDp(worldSizeWidth.toInt()),
                pixelToDp(worldSizeHeight.toInt())
            )
                .zIndex(2f)
        ) {
            val stepPx = GRID_STEP_CM * scale // Khoảng cách lưới trên màn hình
            // Tính toán vùng nhìn thấy để chỉ vẽ những gì cần thiết (Tối ưu hiệu năng)
            val startX = (-offset.x / stepPx).toInt().coerceAtLeast(0)
            val endX =
                (startX + (viewportSize.width / stepPx).toInt() + 1).coerceAtMost((worldSizeWidth / GRID_STEP_CM).toInt())

            val startY = (-offset.y / stepPx).toInt().coerceAtLeast(0)
            val endY =
                (startY + (viewportSize.height / stepPx).toInt() + 1).coerceAtMost((worldSizeHeight / GRID_STEP_CM).toInt())

            drawRect(
                color = Color(0xBFFFFFFF),
                topLeft = offset,
                size = Size(
                    worldSizeWidth * scale,
                    worldSizeHeight * scale
                ),
            )
            // Vẽ đường dọc
            if (scale > 1.7f && state.isShowGrid) {

                for (i in startX..endX) {
                    val xPos = (i * GRID_STEP_CM * scale) + offset.x
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = Offset(xPos, 0f + offset.y), // Giới hạn trong vùng vẽ
                        end = Offset(xPos, worldSizeHeight * scale + offset.y),
                        strokeWidth = 1f
                    )
                }

                // Vẽ đường ngang
                for (i in startY..endY) {
                    val yPos = (i * GRID_STEP_CM * scale) + offset.y
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = Offset(0f + offset.x, yPos),
                        end = Offset(worldSizeWidth * scale + offset.x, yPos),
                        strokeWidth = 1f
                    )
                }
            }

            // Vẽ biên giới hạn 20m x 20m
            drawRect(
                color = DarkGrey,
                topLeft = offset,
                size = Size(
                    worldSizeWidth * scale,
                    worldSizeHeight * scale
                ),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // --- LAYER 2: VẬT THỂ ---
        objects.forEach { obj ->
            key(obj.id) {
                // Tính toán vị trí hiển thị trên màn hình
                // Công thức: (Tọa độ thực * Scale) + Offset Camera

                var dragPos by remember(obj.id) {
                    mutableStateOf(Offset(obj.posX.toFloat(), obj.posY.toFloat()))
                }
                LaunchedEffect(obj.posX, obj.posY) {
                    dragPos = Offset(
                        obj.posX.toFloat().coerceIn(-200f, worldSizeWidth - obj.width + 200f),
                        obj.posY.toFloat().coerceIn(-200f, worldSizeHeight - obj.height + 200f)
                    )
                }
                val screenX = (dragPos.x * scale) + offset.x
                val screenY = (dragPos.y * scale) + offset.y
                val objW = (if (!obj.isRotated) obj.width else obj.height) * scale
                val objH = (if (!obj.isRotated) obj.height else obj.width) * scale

                // 🔥 CULLING Ở ĐÂY
                if (!isVisible(screenX, screenY, objW, objH, viewportSize)) return@forEach
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                        .offset { IntOffset(screenX.roundToInt(), screenY.roundToInt()) }
                        .requiredSize(
                            pixelToDp(if (!obj.isRotated) obj.width else obj.height) * scale,
                            pixelToDp(if (!obj.isRotated) obj.height else obj.width) * scale
                        )
                        .background(
                            obj.userGroup?.getString("colorCode")?.toColor()
                                ?: if (obj.objectType == "TABLE" || obj.objectType == "OBJECT") MaterialTheme.colorScheme.surfaceVariant else Color.White,
                        )
                        .border(
                            width = 1.dp,
                            color = if (obj.id == state.selectedObjectId || state.selectedObjectIdList.contains(
                                    obj.id
                                )
                            ) Color.Green else Color.Black
                        )
                        .zIndex(if (obj.objectType == "LABEL") 4f else if (obj.id == state.selectedObjectId) 3f else 2f)
                        .clickable() {
                            viewModel.setSelectedObjectId(obj.id)
                            viewModel.setSelectedObjectIdList(emptyList())
                            viewModel.setScale(scale)
                            viewModel.setOffset(offset)
                        }
                        .pointerInput(state.isStylus, state.scale) {
                            detectDragGestures(
                                onDragStart = {
                                    viewModel.setSelectedObjectId(obj.id)
                                    viewModel.setSelectedObjectIdList(emptyList())
                                },
                                onDragEnd = {
                                    val snappedX =
                                        (dragPos.x / SNAP_STEP_CM).roundToInt() * SNAP_STEP_CM
                                    val snappedY =
                                        (dragPos.y / SNAP_STEP_CM).roundToInt() * SNAP_STEP_CM

                                    viewModel.updateObjectPositionOnly(
                                        obj.id,
                                        snappedX.toInt(),
                                        snappedY.toInt()
                                    )
                                },

                                onDrag = { change, dragAmount ->
                                    val canDrag =
                                        !state.isStylus || change.type == PointerType.Stylus

                                    if (canDrag) {
                                        change.consume()
                                        dragPos += dragAmount / state.scale
                                    }
                                }

                            )
                            detectTapGestures(
                                onTap = {
                                    viewModel.setScale(scale)
                                    viewModel.setOffset(offset)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {

                    ObjectContent(obj, scale)
                    FloatingAction(scale, state, obj, onDelete, onOpenDetail, viewModel, dragPos)
                }
            }
        }
        if (state.selectedObjectId != 0 && scale > 3f) {
            val selectedObj = objects.find { it.id == state.selectedObjectId }
            selectedObj?.let { obj ->

                val screenX = (obj.posX * scale) + offset.x
                val screenY = (obj.posY * scale) + offset.y
                val objW = (if (!obj.isRotated) obj.width else obj.height) * scale
                val objH = (if (!obj.isRotated) obj.height else obj.width) * scale


                Box(
                    modifier = Modifier
                        .offset { IntOffset(screenX.roundToInt(), screenY.roundToInt()) }
                        .width(pixelToDp(objW.toInt()))
                        .height(pixelToDp(objH.toInt())).zIndex(2f)
                ) {
                    // Gọi hàm FloatingAction của bạn tại đây
//                    FloatingAction(
//                        scale,
//                        state,
//                        obj,
//                        onDelete,
//                        onOpenDetail,
//                        viewModel,
//                        Offset(obj.posX.toFloat(), obj.posY.toFloat())
//                    )
                }
            }
        }
        Column(
            modifier = Modifier.align(Alignment.TopEnd).zIndex(4f),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "${((scale * 100).toInt()).toFloat() / 100}x",
                modifier = Modifier
                    .width(110.dp)
                    .wrapContentHeight(),
                textAlign = TextAlign.Right,
                maxLines = 1
            )
            Text(
                text = "${offset.x.toInt()},${offset.y.toInt()}",
                modifier = Modifier
                    .width(110.dp)
                    .wrapContentHeight(),
                textAlign = TextAlign.Right,
                maxLines = 1
            )

            IconButton(
                onClick = {
                    scale = 1f
                    offset = Offset.Zero
                    viewModel.setOffset(Offset.Zero)
                    viewModel.setScale(1f)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    painterResource(Res.drawable.ic_oui_refresh),
                    contentDescription = "reset"
                )
            }
            IconButton(
                onClick = {
                    viewModel.setIsStylus(!state.isStylus)
                    viewModel.setSelectedObjectId(0)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        if (state.isStylus) Color.LightGray else Color.White,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                if (state.isStylus) {
                    Icon(
                        painterResource(Res.drawable.hand_gesture_off),
                        contentDescription = "unlock"
                    )
                } else {
                    Icon(
                        painterResource(Res.drawable.hand_gesture),
                        contentDescription = "lock"
                    )
                }

            }


            IconButton(
                onClick = {
                    viewModel.setIsBgImg(!state.isBgImg)
                },
                modifier = Modifier
                    .padding(8.dp)
                    // [+] THÊM: Đổi màu nền thành Vàng khi đang kích hoạt
                    .background(
                        if (state.isBgImg) Color.Yellow else Color.White,
                        shape = MaterialTheme.shapes.small
                    ),
                enabled = state.bgBitmap != null
            ) {
                if (state.isBgImg) {
                    PairIcon(Res.drawable.hand_gesture, Res.drawable.image)
                } else {
                    PairIcon(Res.drawable.hand_gesture_off, Res.drawable.image)
                }
            }

            IconButton(
                onClick = {
                    viewModel.setIsShowBg(!state.isShowBg)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small),
                enabled = state.bgBitmap != null
            ) {
                if (state.isShowBg) {
                    PairIcon(Res.drawable.image, Res.drawable.visibility)
                } else {
                    PairIcon(Res.drawable.image, Res.drawable.visibility_off)
                }
            }
            IconButton(
                onClick = {
                    viewModel.setIsShowGrid(!state.isShowGrid)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
            ) {
                if (state.isShowGrid) {
                    Icon(painterResource(Res.drawable.grid_3x3), contentDescription = "grid")
                } else {
                    Icon(
                        painterResource(Res.drawable.grid_3x3_off),
                        contentDescription = "grid off"
                    )
                }
            }
            IconButton(
                onClick = {
                    scope.launch {
                        val imageFile = FileKit.openFilePicker(type = FileKitType.Image)
                        imageFile?.readBytes()?.let { bytes ->
                            // Decode ảnh
                            val newBitmap = bytes.decodeToImageBitmap()

                            // Cập nhật State cục bộ
                            bgBitmap = newBitmap

                            // Cập nhật ViewModel (Phải gọi BÊN TRONG coroutine sau khi có ảnh)
                            viewModel.setBackgroundImage(newBitmap)
                        }
                    }
                    viewModel.setBackgroundImage(bgBitmap)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
            ) {
                Icon(painterResource(Res.drawable.add_photo), contentDescription = "add photo")
            }


        }


    }

}

@Composable
private fun BoxScope.FloatingAction(
    scale: Float,
    state: EditorState,
    obj: MapObjectDto,
    onDelete: () -> Unit,
    onOpenDetail: () -> Unit,
    viewModel: EditorViewModel,
    dragPos: Offset
) {
    if (scale > 3f) {
        if (state.selectedObjectId == obj.id) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
                    .offset(pixelToDp(60), -pixelToDp(60))
                    .background(Color.Red, shape = CircleShape)
                    .size(pixelToDp(14) * scale).zIndex(2f)

            ) {
                Icon(painterResource(Res.drawable.close), contentDescription = "delete")
            }
            IconButton(
                onClick = onOpenDetail,
                modifier = Modifier.align(Alignment.TopStart)
                    .offset(-pixelToDp(60), -pixelToDp(60))
                    .background(Color.Cyan, shape = CircleShape)
                    .size(pixelToDp(14) * scale)

            ) {
                Icon(
                    painterResource(Res.drawable.edit_note),
                    contentDescription = "edit"
                )
            }
            IconButton(
                onClick = {
                    viewModel.duplicate(Direction.UP)
                },
                modifier = Modifier.align(Alignment.TopCenter)
                    .offset(0.dp, -pixelToDp(60))
                    .background(Color.Cyan, shape = CircleShape)
                    .size(pixelToDp(14) * scale)

            ) {
                Icon(
                    painterResource(Res.drawable.add_row_above),
                    contentDescription = "duplicate up"
                )
            }
            IconButton(
                onClick = {
                    viewModel.duplicate(Direction.LEFT)
                },
                modifier = Modifier.align(Alignment.CenterStart)
                    .offset(-pixelToDp(60), 0.dp)
                    .background(Color.Cyan, shape = CircleShape)
                    .size(pixelToDp(14) * scale)
            ) {
                Icon(
                    painterResource(Res.drawable.add_row_above),
                    contentDescription = "duplicate left",
                    modifier = Modifier.rotate(-90f)
                )
            }
            IconButton(
                onClick = {
                    viewModel.duplicate(Direction.DOWN)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
                    .offset(0.dp, pixelToDp(60))
                    .background(Color.Cyan, shape = CircleShape)
                    .size(pixelToDp(14) * scale)
            ) {
                Icon(
                    painterResource(Res.drawable.add_row_above),
                    contentDescription = "duplicate down",
                    modifier = Modifier.rotate(180f)
                )
            }
            IconButton(
                onClick = {
                    viewModel.duplicate(Direction.RIGHT)
                },
                modifier = Modifier.align(Alignment.CenterEnd)
                    .offset(pixelToDp(60), 0.dp)
                    .background(Color.Cyan, shape = CircleShape)
                    .size(pixelToDp(14) * scale)
            ) {
                Icon(
                    painterResource(Res.drawable.add_row_above),
                    contentDescription = "duplicate right",
                    modifier = Modifier.rotate(90f)
                )
            }
        }

        Text(
            text = "${dragPos.x.toInt()},${dragPos.y.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

fun isVisible(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    viewport: IntSize
): Boolean {
    return !(
            x + w < 0f ||
                    y + h < 0f ||
                    x > viewport.width ||
                    y > viewport.height
            )
}


