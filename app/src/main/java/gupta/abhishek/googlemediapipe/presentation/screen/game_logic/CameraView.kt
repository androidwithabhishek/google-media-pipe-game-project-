package gupta.abhishek.googlemediapipe.presentation.screen.game_logic



import android.content.Context
import android.hardware.display.DisplayManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandAnalyzer
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper

// ---------------------------------------------------------------------
// CameraView
//   Camera setup. Also tells the frame grabber when the phone rotates,
//   so MediaPipe always gets an upright picture.
// ---------------------------------------------------------------------
@Composable
internal fun CameraView(helper: HandLandmarkerHelper) {

    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }

    // Watches screen rotation and passes it to the frame grabber.
    DisposableEffect(Unit) {

        val displayManager =
            context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val displayListener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {}
            override fun onDisplayRemoved(displayId: Int) {}
            override fun onDisplayChanged(displayId: Int) {
                val display = previewView.display
                if (display != null) {
                    imageAnalyzer.targetRotation = display.rotation
                }
            }
        }

        displayManager.registerDisplayListener(displayListener, null)

        onDispose { displayManager.unregisterDisplayListener(displayListener) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                preview.surfaceProvider = previewView.surfaceProvider

                previewView.display?.let { imageAnalyzer.targetRotation = it.rotation }

                imageAnalyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    HandAnalyzer(helper)
                )

                // Front camera. Use DEFAULT_BACK_CAMERA for the back camera.
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    ctx as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}