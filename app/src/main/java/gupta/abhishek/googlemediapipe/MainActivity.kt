package gupta.abhishek.googlemediapipe

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.presentation.CameraPreview
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandOverlay


class MainActivity : ComponentActivity() {

    private lateinit var helper: HandLandmarkerHelper

    private var handResult by mutableStateOf<HandLandmarkerResult?>(null)

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startApplication()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    private fun startApplication() {

        helper =
            HandLandmarkerHelper(
                context = this,

                onResult = { result ->

                    runOnUiThread {
                        handResult = result
                    }
                },

                onError = { error ->

                    error.prependIndent()
                }
            )

        helper.setup()

        setContent {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                CameraPreview(
                    helper = helper,
                    modifier = Modifier.fillMaxSize()
                )

                HandOverlay(
                    result = handResult,
                    modifier = Modifier.fillMaxSize()
                )

                if (handResult?.landmarks()?.isEmpty() != false) {

                    Text(
                        text = "Show your hand to the camera",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(
                                horizontal = 20.dp,
                                vertical = 12.dp
                            )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::helper.isInitialized) {
            helper.close()
        }
    }




}
