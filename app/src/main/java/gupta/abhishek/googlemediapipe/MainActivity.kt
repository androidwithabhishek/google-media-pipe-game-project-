package gupta.abhishek.googlemediapipe

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandOverlay
import gupta.abhishek.googlemediapipe.presentation.CameraPreview


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

        // Always landscape. SENSOR_LANDSCAPE = landscape both ways up (flip the phone
        // 180 degrees and it follows), but it never goes to portrait.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

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
                    handResult = handResult,   // game needs the hand points
                    modifier = Modifier.fillMaxSize()
                )

                HandOverlay(
                    result = handResult,
                    modifier = Modifier.fillMaxSize()
                )
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