package gupta.abhishek.googlemediapipe

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import dagger.hilt.android.AndroidEntryPoint
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.presentation.screen.FruitTestScreen
import gupta.abhishek.googlemediapipe.presentation.screen.MainScreen
import java.util.concurrent.Executors

// true = open the fruit test screen first. false = open the game first.
private const val SHOW_FRUIT_TEST = false

// true = show the hand animation (skeleton/overlay) on screen
// false = hide it, but the hand analyser still runs in the background
private const val SHOW_HAND_ANIMATION = false

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var showFruitTest by mutableStateOf(SHOW_FRUIT_TEST)

    // Latest hand result. Updated from the analyser thread through runOnUiThread.
    private var handResult by mutableStateOf<HandLandmarkerResult?>(null)

    // The hand analyser object is cheap to create. Its model loads only when the camera
    // is first used, so the game starts without waiting for the camera.
    private val helper = HandLandmarkerHelper(
        context = this,
        onResult = { result -> runOnUiThread { handResult = result } },
        onError = { error -> Log.e("HandLandmarker", error) }
    )

    // Loads the model on a background thread, so turning the camera on does not freeze the game.
    private val setupExecutor = Executors.newSingleThreadExecutor()
    private var cameraPrepared = false

    // Called when the camera is needed for the first time. Safe to call again.
    private fun prepareCamera() {
        if (cameraPrepared) return
        cameraPrepared = true
        setupExecutor.execute { helper.setup() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Always landscape. SENSOR_LANDSCAPE = landscape both ways up, never portrait.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Keep the screen on while the app is open, so it never turns off by the screen timeout.
//         window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // No permission request here: the game always starts. Camera permission is asked
        // only when the player turns camera mode on (see MainScreen).
        setContent {
            if (showFruitTest) {
                FruitTestScreen(
                    onGoToGame = { showFruitTest = false }
                )
            } else {
                MainScreen(
                    helper = helper,
                    handResult = handResult,
                    showHandAnimation = SHOW_HAND_ANIMATION,
                    onCameraNeeded = { prepareCamera() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (cameraPrepared) {
            // Runs after the setup on the same thread, then the executor stops.
            setupExecutor.execute { helper.close() }
        }
        setupExecutor.shutdown()
    }
}