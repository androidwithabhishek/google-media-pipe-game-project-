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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// ---------------------------------------------------------------------
// CameraView
//   Camera picture + hand analysis. Only composed when hand control is ON.
//   When it leaves the screen, the camera is switched off (saves battery and CPU).
// ---------------------------------------------------------------------
@Composable
internal fun CameraView(helper: HandLandmarkerHelper) {

    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    // Hand analysis runs on this background thread, NOT on the UI thread.
    val analysisExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Keeps the camera provider so the camera can be turned off later.
    val cameraProviderHolder = remember { arrayOfNulls<ProcessCameraProvider>(1) }

    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }

    // Watches screen rotation, and turns the camera off when this view leaves the screen.
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

        onDispose {
            displayManager.unregisterDisplayListener(displayListener)
            cameraProviderHolder[0]?.unbindAll()     // camera OFF
            analysisExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()
                cameraProviderHolder[0] = cameraProvider

                val preview = Preview.Builder().build()
                preview.surfaceProvider = previewView.surfaceProvider

                previewView.display?.let { imageAnalyzer.targetRotation = it.rotation }

                imageAnalyzer.setAnalyzer(
                    analysisExecutor,
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
