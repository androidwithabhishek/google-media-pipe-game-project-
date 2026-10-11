package gupta.abhishek.googlemediapipe.core.google_media_pipe

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class HandAnalyzer(
    private val helper: HandLandmarkerHelper
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {

        try {

            val bitmap = imageProxy.toBitmap()

            val rotatedBitmap =
                rotateBitmap(
                    bitmap,
                    imageProxy.imageInfo.rotationDegrees
                )

            helper.detect(
                rotatedBitmap,
                System.currentTimeMillis()
            )

        } catch (e: Exception) {

            e.printStackTrace()

        } finally {

            imageProxy.close()
        }
    }

    private fun rotateBitmap(
        bitmap: Bitmap,
        rotation: Int
    ): Bitmap {

        if (rotation == 0) {
            return bitmap
        }

        val matrix =
            Matrix()

        matrix.postRotate(
            rotation.toFloat()
        )

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}