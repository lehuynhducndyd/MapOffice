package com.example.ar2

import android.content.Context
import android.net.Uri
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.exceptions.NotYetAvailableException
import java.nio.FloatBuffer

class DepthData private constructor(
    val points: FloatBuffer,
    val colors: FloatBuffer,
    val timestamp: Long,
    val anchor: Anchor
) {
    companion object {
        const val MAX_POINTS = 8000

        fun create(session: Session, frame: Frame): DepthData? {
            try {
                frame.acquireCameraImage().use { cameraImage ->
                    frame.acquireRawDepthImage16Bits().use { depthImage ->
                        frame.acquireRawDepthConfidenceImage().use { confidenceImage ->

                            val intrinsics = frame.camera.textureIntrinsics
                            val points = PointCloudHelper.convertRawDepthImagesTo3dPointBuffer(
                                depthImage, confidenceImage, intrinsics, MAX_POINTS
                            )

                            val imageRegionCoordinates = PointCloudHelper.getImageCoordinatesForFullTexture(frame)
                            val colors = PointCloudHelper.convertImageToColorBuffer(
                                cameraImage, depthImage, imageRegionCoordinates, MAX_POINTS
                            )

                            val cameraPoseAnchor = session.createAnchor(frame.camera.pose)
                            return DepthData(points, colors, depthImage.timestamp, cameraPoseAnchor)
                        }
                    }
                }
            } catch (e: NotYetAvailableException) {
                // Dữ liệu depth chưa sẵn sàng
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
