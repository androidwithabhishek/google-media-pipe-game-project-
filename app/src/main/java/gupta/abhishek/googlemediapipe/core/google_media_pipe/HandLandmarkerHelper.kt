package gupta.abhishek.googlemediapipe.core.google_media_pipe

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    private val context: Context,
    private val onResult: (HandLandmarkerResult) -> Unit,
    private val onError: (String) -> Unit
) {

    @Volatile
    private var handLandmarker: HandLandmarker? = null

    fun setup() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .setDelegate(Delegate.CPU)
                .build()

            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumHands(2)
                .setMinHandDetectionConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setResultListener { result, _ -> onResult(result) }
                .setErrorListener { error -> onError(error.message ?: "Unknown MediaPipe error") }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            onError(e.message ?: "Unable to initialize MediaPipe")
        }
    }

    // rotationDegrees: 0, 90, 180 or 270. MediaPipe turns the picture upright itself.
    fun detect(bitmap: Bitmap, rotationDegrees: Int, timestamp: Long) {
        val image = BitmapImageBuilder(bitmap).build()
        val options = ImageProcessingOptions.builder()
            .setRotationDegrees(rotationDegrees)
            .build()
        handLandmarker?.detectAsync(image, options, timestamp)
    }

    fun close() {
        handLandmarker?.close()
        handLandmarker = null
    }
}
