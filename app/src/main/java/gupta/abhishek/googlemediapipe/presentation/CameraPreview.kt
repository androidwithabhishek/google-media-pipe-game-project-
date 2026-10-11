package gupta.abhishek.googlemediapipe.presentation

import androidx.core.content.ContextCompat

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandAnalyzer
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper


@Composable
fun CameraPreview(
    helper: HandLandmarkerHelper,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->

            val previewView =
                PreviewView(context)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({

                val cameraProvider =
                    cameraProviderFuture.get()

                val preview =
                    Preview.Builder()
                        .build()

                preview.surfaceProvider =
                    previewView.surfaceProvider

                val analyzer =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .setOutputImageFormat(
                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
                        )
                        .build()

                analyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    HandAnalyzer(helper)
                )

                val cameraSelector =
                    CameraSelector.DEFAULT_FRONT_CAMERA

//                val cameraSelector =
//                    CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    preview,
                    analyzer
                )

            }, ContextCompat.getMainExecutor(context))

            previewView
        }
    )
}