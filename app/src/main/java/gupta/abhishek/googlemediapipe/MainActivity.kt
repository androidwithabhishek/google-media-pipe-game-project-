package gupta.abhishek.googlemediapipe

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import dagger.hilt.android.AndroidEntryPoint
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.presentation.FruitTestScreen

import gupta.abhishek.googlemediapipe.presentation.screen.MainScreen

// true = open the fruit test screen first. false = open the game first.
private const val SHOW_FRUIT_TEST = false

// true = show the hand animation (skeleton/overlay) on screen
// false = hide it, but the hand analyser still runs in the background
private const val SHOW_HAND_ANIMATION = false

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var helper: HandLandmarkerHelper

    private var handResult by mutableStateOf<HandLandmarkerResult?>(null)

    // Which screen is showing right now.
    private var showFruitTest by mutableStateOf(SHOW_FRUIT_TEST)

    // True once the camera and hand analyser are ready.
    private var cameraReady by mutableStateOf(false)

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

        // Always landscape. SENSOR_LANDSCAPE = landscape both ways up,
        // but it never goes to portrait.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Keep the screen on while the app is open, so it never turns off by the screen timeout.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            if (showFruitTest) {
                FruitTestScreen(
                    onGoToGame = { goToGame() }
                )
            } else if (cameraReady) {
                MainScreen(
                    helper = helper,
                    handResult = handResult,
                    showHandAnimation = SHOW_HAND_ANIMATION
                )
            }
        }

        // If the app starts on the game, go there right away.
        if (!showFruitTest) {
            goToGame()
        }
    }

    // Switches from the test screen to the game. Asks for camera permission the first time.
    private fun goToGame() {
        showFruitTest = false

        if (!cameraReady) {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
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

        cameraReady = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::helper.isInitialized) {
            helper.close()
        }
    }
}