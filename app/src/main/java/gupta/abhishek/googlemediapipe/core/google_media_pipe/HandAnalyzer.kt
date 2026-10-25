package gupta.abhishek.googlemediapipe.core.google_media_pipe

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

// Runs on a background thread (see CameraView). It does NOT rotate the picture itself:
// MediaPipe rotates it with the rotation we pass, which saves a full bitmap copy per frame.
class HandAnalyzer(
    private val helper: HandLandmarkerHelper
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            helper.detect(
                bitmap = bitmap,
                rotationDegrees = imageProxy.imageInfo.rotationDegrees,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }
}
