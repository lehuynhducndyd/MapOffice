package com.example.ar2

import android.media.Image
import com.google.ar.core.CameraIntrinsics
import com.google.ar.core.Coordinates2d
import com.google.ar.core.Frame
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


object PointCloudHelper {
    const val POSITION_FLOATS_PER_POINT = 4 // X, Y, Z, Confidence
    const val COLOR_FLOATS_PER_POINT = 3    // R, G, B

    private val TEXTURE_COORDS = floatArrayOf(
        0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    )

    fun convertRawDepthImagesTo3dPointBuffer(
        depth: Image,
        confidence: Image,
        cameraTextureIntrinsics: CameraIntrinsics,
        pointLimit: Int
    ): FloatBuffer {
        val depthImagePlane = depth.planes[0]
        val depthBuffer = depthImagePlane.buffer.order(ByteOrder.nativeOrder()).asShortBuffer()

        val confidenceImagePlane = confidence.planes[0]
        val confidenceBuffer = confidenceImagePlane.buffer.order(ByteOrder.nativeOrder())

        val intrinsicsDimensions = cameraTextureIntrinsics.imageDimensions
        val depthWidth = depth.width
        val depthHeight = depth.height
        val fx = cameraTextureIntrinsics.focalLength[0] * depthWidth / intrinsicsDimensions[0]
        val fy = cameraTextureIntrinsics.focalLength[1] * depthHeight / intrinsicsDimensions[1]
        val cx = cameraTextureIntrinsics.principalPoint[0] * depthWidth / intrinsicsDimensions[0]
        val cy = cameraTextureIntrinsics.principalPoint[1] * depthHeight / intrinsicsDimensions[1]

        val step = calculateImageSubsamplingStep(depthWidth, depthHeight, pointLimit)
        val capacity = (depthWidth / step) * (depthHeight / step) * POSITION_FLOATS_PER_POINT
        val points = ByteBuffer.allocateDirect(capacity * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        for (y in 0 until depthHeight step step) {
            for (x in 0 until depthWidth step step) {
                val depthMillimeters = depthBuffer.get(y * depthWidth + x).toInt() and 0xFFFF
                if (depthMillimeters == 0) continue

                val depthMeters = depthMillimeters / 1000.0f

                points.put(depthMeters * (x - cx) / fx) // X
                points.put(depthMeters * (cy - y) / fy) // Y
                points.put(-depthMeters)                // Z

                val confidencePixelValue = confidenceBuffer.get(
                    y * confidenceImagePlane.rowStride + x * confidenceImagePlane.pixelStride
                )
                val confidenceNormalized = (confidencePixelValue.toInt() and 0xFF) / 255.0f
                points.put(confidenceNormalized)        // Confidence
            }
        }
        points.rewind()
        return points
    }

    fun getImageCoordinatesForFullTexture(frame: Frame): FloatBuffer {
        val textureCoords = ByteBuffer.allocateDirect(TEXTURE_COORDS.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(TEXTURE_COORDS)
        textureCoords.position(0)

        val imageCoords = ByteBuffer.allocateDirect(TEXTURE_COORDS.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        frame.transformCoordinates2d(
            Coordinates2d.TEXTURE_NORMALIZED, textureCoords,
            Coordinates2d.IMAGE_PIXELS, imageCoords
        )
        return imageCoords
    }

    fun convertImageToColorBuffer(
        color: Image,
        depth: Image,
        imageCoords: FloatBuffer,
        pointLimit: Int
    ): FloatBuffer {
        val depthWidth = depth.width
        val depthHeight = depth.height
        val colorWidth = color.width

        val imagePlaneY = color.planes[0]
        val imagePlaneU = color.planes[1]
        val imagePlaneV = color.planes[2]

        val rowStrideY = imagePlaneY.rowStride
        val rowStrideU = imagePlaneU.rowStride
        val rowStrideV = imagePlaneV.rowStride

        val pixelStrideY = imagePlaneY.pixelStride
        val pixelStrideU = imagePlaneU.pixelStride
        val pixelStrideV = imagePlaneV.pixelStride

        val colorBufferY = imagePlaneY.buffer
        val colorBufferU = imagePlaneU.buffer
        val colorBufferV = imagePlaneV.buffer

        val colorMinY = Math.round(imageCoords.get(1))
        val colorMaxY = Math.round(imageCoords.get(3))
        val colorRegionHeight = colorMaxY - colorMinY

        val depthImagePlane = depth.planes[0]
        val depthBuffer = depthImagePlane.buffer.order(ByteOrder.nativeOrder()).asShortBuffer()

        val step = calculateImageSubsamplingStep(depthWidth, depthHeight, pointLimit)
        val capacity = (depthWidth / step) * (depthHeight / step) * COLOR_FLOATS_PER_POINT
        val colors = ByteBuffer.allocateDirect(capacity * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        val rgb = FloatArray(3)

        for (y in 0 until depthHeight step step) {
            for (x in 0 until depthWidth step step) {
                if (depthBuffer.get(y * depthWidth + x).toInt() == 0) continue

                val colorX = x * colorWidth / depthWidth
                val colorY = colorMinY + y * colorRegionHeight / depthHeight
                val colorHalfX = colorX / 2
                val colorHalfY = colorY / 2

                val channelValueY = colorBufferY.get(colorY * rowStrideY + colorX * pixelStrideY).toInt() and 0xFF
                val channelValueU = colorBufferU.get(colorHalfY * rowStrideU + colorHalfX * pixelStrideU).toInt() and 0xFF
                val channelValueV = colorBufferV.get(colorHalfY * rowStrideV + colorHalfX * pixelStrideV).toInt() and 0xFF

                convertYuvToRgb(channelValueY, channelValueU, channelValueV, rgb)
                colors.put(rgb[0])
                colors.put(rgb[1])
                colors.put(rgb[2])
            }
        }
        colors.rewind()
        return colors
    }

    private fun calculateImageSubsamplingStep(imageWidth: Int, imageHeight: Int, n: Int): Int {
        return ceil(sqrt((imageWidth.toFloat() * imageHeight) / n)).toInt()
    }

    private fun convertYuvToRgb(yInt: Int, uInt: Int, vInt: Int, rgb: FloatArray) {
        val yFloat = yInt / 255.0f
        val uFloat = uInt * 0.872f / 255.0f - 0.436f
        val vFloat = vInt * 1.230f / 255.0f - 0.615f
        rgb[0] = clamp(yFloat + 1.13983f * vFloat)
        rgb[1] = clamp(yFloat - 0.39465f * uFloat - 0.58060f * vFloat)
        rgb[2] = clamp(yFloat + 2.03211f * uFloat)
    }

    private fun clamp(value: Float): Float = max(0.0f, min(1.0f, value))


}