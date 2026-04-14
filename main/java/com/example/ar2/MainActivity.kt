package com.example.ar2

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.filament.*
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARScene
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberScene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Size
import io.github.sceneview.node.PlaneNode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RawDepthApp()
        }
    }
}

@Composable
fun RawDepthApp() {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val scene = rememberScene(engine)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pointAmount by remember { mutableFloatStateOf(1f) }
    var logTrackingState by remember { mutableStateOf("N/A") }
    var logPointsCount by remember { mutableIntStateOf(0) }
    var logFrameCount by remember { mutableIntStateOf(0) }
    val materialInstance = remember(materialLoader) {
        materialLoader.createColorInstance(Color.White)
    }

// ---------------------------------------------------------------------
// BỘ PHẬN LƯU TRỮ VÀ LỌC DỮ LIỆU
// ---------------------------------------------------------------------
    val maxFrames = 25
    val storedFrames = remember { mutableStateListOf<DepthFrameNode>() }
    var lastCapturedPose by remember { mutableStateOf<Pose?>(null) }

// Kho Lưới không gian (Voxel Grid) lọc điểm trùng
    val occupiedVoxels = remember { HashSet<Long>() }

// KHO CHỨA TỔNG (Lưu điểm 3D sạch để XUẤT FILE)
    val exportedPointCloud = remember { ArrayList<Point3D>() }
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri: Uri? ->
            if (uri != null) {
// Khi người dùng chọn chỗ lưu xong -> Bắt đầu ghi file
                Toast.makeText(context, "Đang lưu file...", Toast.LENGTH_SHORT).show()
                coroutineScope.launch(Dispatchers.IO) {
                    exportPointCloudToUri(context, exportedPointCloud, uri)
                }
            } else {
                Toast.makeText(context, "Đã hủy lưu file", Toast.LENGTH_SHORT).show()
            }
        }
    )

    DisposableEffect(scene) {
        onDispose {
            storedFrames.forEach {
                scene.removeEntity(it.entity)
                it.releaseResources(engine)
            }
            storedFrames.clear()
            occupiedVoxels.clear()
            exportedPointCloud.clear()
        }
    }

    val cameraNode = rememberARCameraNode(engine)
    val dimMaterial = remember(materialLoader) {
        materialLoader.createColorInstance(Color.Black.copy(alpha = 0.85f))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            materialLoader = materialLoader,
            scene = scene,
            planeRenderer = false,
            cameraNode = cameraNode,
            sessionConfiguration = { session, config ->
                when {
                    session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY) -> config.depthMode = Config.DepthMode.RAW_DEPTH_ONLY
                    session.isDepthModeSupported(Config.DepthMode.AUTOMATIC) -> config.depthMode = Config.DepthMode.AUTOMATIC
                }
            },
            onSessionUpdated = { session, frame ->
                try {
                    val camera = frame.camera
                    logTrackingState = camera.trackingState.name
                    if (camera.trackingState == TrackingState.TRACKING) {
                        val currentPose = camera.pose

// QUÉT TỚI ĐÂU RENDER TỚI ĐÓ (Lia máy > 2cm hoặc xoay > 2 độ mới xử lý)
                        val shouldCapture = lastCapturedPose == null ||
                                getTranslationDistance(lastCapturedPose!!, currentPose) > 0.05f ||
                                getRotationAngle(lastCapturedPose!!, currentPose) > 5.0f

                        if (shouldCapture) {
                            val depthData = DepthData.create(session, frame)

                            if (depthData != null) {
                                lastCapturedPose = currentPose
                                val frameNode = DepthFrameNode(
                                    engine = engine,
                                    materialInstance = materialInstance,
                                    depthData = depthData,
                                    pointAmount = pointAmount,
                                    occupiedVoxels = occupiedVoxels,
                                    exportedPointCloud = exportedPointCloud // Truyền kho lưu xuống Node
                                )

// Chỉ thêm vào lưới hiển thị nếu CÓ ĐIỂM MỚI
                                if (frameNode.isValid) {
                                    scene.addEntity(frameNode.entity)
                                    storedFrames.add(frameNode)
                                    if (storedFrames.size > maxFrames) {
                                        val oldest = storedFrames.removeAt(0)
                                        scene.removeEntity(oldest.entity)
                                        oldest.releaseResources(engine)
                                        oldest.depthData.anchor.detach()
                                    }
                                    logFrameCount = storedFrames.size
                                    logPointsCount = exportedPointCloud.size
                                } else {
                                    frameNode.releaseResources(engine)
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}

            }
        ) {
            // 👉 HOÀN THIỆN LỚP NỀN ĐEN: Che ống kính camera để tạo cảm giác quét chuyên nghiệp
            PlaneNode(
                size = Size(x = 100f, y = 100f),
                position = Position(x = 0f, y = 0f, z = -10f), // Lùi ra 10m phía sau đám mây điểm
                rotation = Rotation(x = 90f, y = 0f, z = 0f),  // Dựng đứng mặt phẳng lên
                materialInstance = dimMaterial,
                apply = {
                    parent = cameraNode // Gắn cứng vào ống kính camera
                }
            )
        }

// BẢNG LOG VÀ NÚT XUẤT FILE (GÓC TRÊN BÊN TRÁI)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(12.dp)

        ) {
            Text("Tracking: $logTrackingState", color = if (logTrackingState == "TRACKING") Color.Green else Color.Red)
            Text("Số khối đã render: $logFrameCount / $maxFrames", color = Color.Cyan)
            Text("Tổng điểm đã lưu (100% tin cậy): $logPointsCount", color = Color.Yellow)
            Spacer(modifier = Modifier.height(12.dp))

// 👉 NÚT XUẤT FILE 3D ĐÃ SỬA
            Button(onClick = {
                if (exportedPointCloud.isEmpty()) {
                    Toast.makeText(context, "Chưa có điểm nào để xuất!", Toast.LENGTH_SHORT).show()
                } else {
// Mở cửa sổ chọn chỗ lưu, đề xuất tên file mặc định
                    saveFileLauncher.launch("Scan3D_${System.currentTimeMillis()}.ply")
                }
            }) {
                Text("Xuất file 3D (.PLY)")
            }
        }

// BẢNG ĐIỀU KHIỂN RENDER (GÓC DƯỚI)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Text(text = "Tỷ lệ vẽ ô vuông (hiển thị): ${"%.2f".format(pointAmount)}", color = Color.White)
            Slider(value = pointAmount, onValueChange = { pointAmount = it }, valueRange = 0f..1f)
            Text(text = "Lưu ý: Độ tin cậy (Confidence) đã được khoá cứng ở mức tối đa (1.0).", color = Color.Gray)
        }
    }
}

// -----------------------------------------------------------------------------------------
// HÀM KIỂM TRA LIA CAMERA
// -----------------------------------------------------------------------------------------
fun getTranslationDistance(pose1: Pose, pose2: Pose): Float {
    val dx = pose1.tx() - pose2.tx()
    val dy = pose1.ty() - pose2.ty()
    val dz = pose1.tz() - pose2.tz()
    return sqrt(dx * dx + dy * dy + dz * dz)
}

fun getRotationAngle(pose1: Pose, pose2: Pose): Float {
    val dot = pose1.qx() * pose2.qx() + pose1.qy() * pose2.qy() + pose1.qz() * pose2.qz() + pose1.qw() * pose2.qw()
    val angle = 2.0 * acos(abs(dot.toDouble()))
    return Math.toDegrees(angle).toFloat()
}

// -----------------------------------------------------------------------------------------
// NODE HIỂN THỊ CÁC "ĐIỂM MỚI" TỪ MỘT FRAME
// -----------------------------------------------------------------------------------------
class DepthFrameNode(
    engine: Engine,
    materialInstance: MaterialInstance,
    val depthData: DepthData,
    pointAmount: Float,
    occupiedVoxels: HashSet<Long>,
    exportedPointCloud: ArrayList<Point3D>,
    squareSize: Float = 0.002f
) : Node(engine = engine) {
    var vertexBuffer: VertexBuffer? = null
    var indexBuffer: IndexBuffer? = null
    var vertexCount: Int = 0
    var isValid: Boolean = false

    init {
        val anchorPose = depthData.anchor.pose
        this.position = Position(anchorPose.tx(), anchorPose.ty(), anchorPose.tz())
        this.quaternion = Quaternion(anchorPose.qx(), anchorPose.qy(), anchorPose.qz(), anchorPose.qw())

        val maxVertices = DepthData.MAX_POINTS * 6
        val posBuffer = ByteBuffer.allocateDirect(maxVertices * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val colBuffer = ByteBuffer.allocateDirect(maxVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val tanBuffer = ByteBuffer.allocateDirect(maxVertices * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()

// Phân tích và CHỈ LẤY ĐIỂM MỚI (Confidence = 1.0) VÀ KHOẢNG CÁCH LÝ TƯỞNG
        val validVertices = processPointsToSquaresFiltered(
            inPoints = depthData.points,
            inColors = depthData.colors,
            outPos = posBuffer,
            outCol = colBuffer,
            outTan = tanBuffer,
            squareSize = squareSize,
            anchorPose = anchorPose,
            occupiedVoxels = occupiedVoxels,
            exportedPointCloud = exportedPointCloud,
            maxDistance = 2.5f // 👉 Giới hạn khoảng cách 2.5 mét
        )

        var pointsToRender = (validVertices * pointAmount).toInt()
        pointsToRender = (pointsToRender / 6) * 6
        vertexCount = pointsToRender

        if (pointsToRender > 0) {
            isValid = true
            vertexBuffer = VertexBuffer.Builder()
                .vertexCount(pointsToRender)
                .bufferCount(3)
                .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT4, 0, 16)
                .attribute(VertexBuffer.VertexAttribute.COLOR, 1, VertexBuffer.AttributeType.FLOAT3, 0, 12)
                .attribute(VertexBuffer.VertexAttribute.TANGENTS, 2, VertexBuffer.AttributeType.FLOAT4, 0, 16)
                .build(engine).apply {
                    setBufferAt(engine, 0, posBuffer)
                    setBufferAt(engine, 1, colBuffer)
                    setBufferAt(engine, 2, tanBuffer)
                }

            val indices = IntArray(pointsToRender) { it }
            val indexByteBuffer = ByteBuffer.allocateDirect(indices.size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
            indexByteBuffer.put(indices).rewind()

            indexBuffer = IndexBuffer.Builder()
                .indexCount(pointsToRender)
                .bufferType(IndexBuffer.Builder.IndexType.UINT)
                .build(engine).apply { setBuffer(engine, indexByteBuffer) }

            RenderableManager.Builder(1)
                .boundingBox(Box(0f, 0f, 0f, 10f, 10f, 10f))
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer!!, indexBuffer!!, 0, pointsToRender)
                .material(0, materialInstance)
                .build(engine, this.entity)
        }
    }

    fun releaseResources(engine: Engine) {
        vertexBuffer?.let { engine.destroyVertexBuffer(it) }
        indexBuffer?.let { engine.destroyIndexBuffer(it) }
        val renderableManager = engine.renderableManager
        if (renderableManager.hasComponent(this.entity)) {
            renderableManager.destroy(this.entity)
        }
    }
}

// -----------------------------------------------------------------------------------------
// THUẬT TOÁN LỌC ĐIỂM: VOXEL GRID, TIN CẬY 1.0 & KHOẢNG CÁCH <= 2.5m
// -----------------------------------------------------------------------------------------
fun processPointsToSquaresFiltered(
    inPoints: FloatBuffer,
    inColors: FloatBuffer,
    outPos: FloatBuffer,
    outCol: FloatBuffer,
    outTan: FloatBuffer,
    squareSize: Float,
    anchorPose: Pose,
    occupiedVoxels: HashSet<Long>,
    exportedPointCloud: ArrayList<Point3D>,
    maxDistance: Float // Nhận khoảng cách tối đa
): Int {
    outPos.clear(); outCol.clear(); outTan.clear()
    inPoints.rewind(); inColors.rewind()
    var validPointsCount = 0
    val s = squareSize / 2f
// Kích thước Voxel 3mm (0.003f)
    val voxelSize = 0.01f
    val ptIn = FloatArray(3)
    val ptOut = FloatArray(3)

    while (inPoints.position() < inPoints.limit() && inColors.position() < inColors.limit()) {
        val x = inPoints.get(); val y = inPoints.get(); val z = inPoints.get()
        val confidence = inPoints.get()
        val r = inColors.get(); val g = inColors.get(); val b = inColors.get()

        // 👉 TÍNH KHOẢNG CÁCH TỪ ĐIỂM ĐẾN CAMERA (Z âm)
        val distance = abs(z)

// KIỂM TRA ĐỘ TIN CẬY TỐI ĐA (1.0f) VÀ KHOẢNG CÁCH LÝ TƯỞNG (<= maxDistance)
        if (confidence >= 1.0f && distance <= maxDistance) {
            ptIn[0] = x; ptIn[1] = y; ptIn[2] = z
            anchorPose.transformPoint(ptIn, 0, ptOut, 0)

            val vx = Math.round(ptOut[0] / voxelSize)
            val vy = Math.round(ptOut[1] / voxelSize)
            val vz = Math.round(ptOut[2] / voxelSize)

            val hash = ((vx.toLong() and 0x1FFFFFL) shl 42) or
                    ((vy.toLong() and 0x1FFFFFL) shl 21) or
                    (vz.toLong() and 0x1FFFFFL)

// Nếu ô không gian này chưa có điểm nào -> LƯU VÀ RENDER
            if (!occupiedVoxels.contains(hash)) {
                occupiedVoxels.add(hash)

// LƯU VÀO KHO TỔNG ĐỂ XUẤT FILE 3D
                exportedPointCloud.add(Point3D(ptOut[0], ptOut[1], ptOut[2], r, g, b))

                val qx = 0f; val qy = 0f; val qz = 0f; val qw = 1f
                val tlX = x - s; val tlY = y + s
                val blX = x - s; val blY = y - s
                val trX = x + s; val trY = y + s
                val brX = x + s; val brY = y - s

                outPos.put(tlX).put(tlY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)
                outPos.put(blX).put(blY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)
                outPos.put(trX).put(trY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)
                outPos.put(trX).put(trY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)
                outPos.put(blX).put(blY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)
                outPos.put(brX).put(brY).put(z).put(1f); outCol.put(r).put(g).put(b); outTan.put(qx).put(qy).put(qz).put(qw)

                validPointsCount++
            }
        }
    }

    outPos.flip(); outCol.flip(); outTan.flip()
    return validPointsCount * 6
}

// -----------------------------------------------------------------------------------------
// DATA CLASS LƯU TRỮ ĐIỂM 3D
// -----------------------------------------------------------------------------------------
data class Point3D(
    val x: Float, val y: Float, val z: Float,
    val r: Float, val g: Float, val b: Float
)

// -----------------------------------------------------------------------------------------
// HÀM XUẤT FILE RA ĐỊNH DẠNG .PLY (BẢN SỬA LỖI LƯU FILE NULL)
// -----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------
// HÀM GHI DỮ LIỆU VÀO FILE THEO VỊ TRÍ NGƯỜI DÙNG ĐÃ CHỌN (SỬ DỤNG URI)
// -----------------------------------------------------------------------------------------
fun exportPointCloudToUri(
    context: Context,
    points: ArrayList<Point3D>,
    uri: Uri
) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { rawOutput ->

            val output = rawOutput.buffered() // 🔥 quan trọng: buffer

            // ---------------- HEADER (BINARY) ----------------
            val header = """
                ply
                format binary_little_endian 1.0
                element vertex ${points.size}
                property float x
                property float y
                property float z
                property uchar red
                property uchar green
                property uchar blue
                end_header
                
            """.trimIndent()

            output.write(header.toByteArray())

            // ---------------- DATA ----------------
            val buffer = ByteBuffer.allocate(15) // 3 float (12) + 3 byte (3)
            buffer.order(ByteOrder.LITTLE_ENDIAN)

            for (p in points) {
                buffer.clear()

                buffer.putFloat(p.x)
                buffer.putFloat(p.y)
                buffer.putFloat(p.z)

                buffer.put((p.r * 255).toInt().coerceIn(0, 255).toByte())
                buffer.put((p.g * 255).toInt().coerceIn(0, 255).toByte())
                buffer.put((p.b * 255).toInt().coerceIn(0, 255).toByte())

                output.write(buffer.array())
            }

            output.flush()
        }

        (context as ComponentActivity).runOnUiThread {
            Toast.makeText(context, "Lưu file thành công (Binary PLY)!", Toast.LENGTH_LONG).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
        (context as ComponentActivity).runOnUiThread {
            Toast.makeText(context, "Lỗi khi lưu file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}