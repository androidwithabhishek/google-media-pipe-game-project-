package gupta.abhishek.googlemediapipe.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandOverlay
import gupta.abhishek.googlemediapipe.core.sound.GameSounds
import gupta.abhishek.googlemediapipe.presentation.MainViewModel

// The real app screen: the game, with the hand overlay on top when enabled.
@Composable
fun MainScreen(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    showHandAnimation: Boolean = true,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bestScore by viewModel.bestScore.collectAsState()

    val settings by viewModel.settings.collectAsState()

    // Sound effects and music. Music pauses in the background and resumes when the app returns.
    val context = LocalContext.current
    val sounds = remember { GameSounds(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        sounds.startMusic()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sounds.startMusic()
                Lifecycle.Event.ON_PAUSE -> sounds.pauseMusic()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sounds.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            helper = helper,
            handResult = handResult,
            modifier = Modifier.fillMaxSize(),
            bestScoreFromDatabase = bestScore,
            onGameOver = { finalScore -> viewModel.saveScore(finalScore) },
            handControlEnabled = settings.handControlEnabled,
            showHandInstruction = !settings.handInstructionSeen,
            onToggleHandControl = { viewModel.setHandControlEnabled(!settings.handControlEnabled) },
            onHandInstructionSeen = { viewModel.markHandInstructionSeen() },
            sounds = sounds
        )

        // The hand overlay only makes sense when the camera is on.
        if (showHandAnimation && settings.handControlEnabled) {
            HandOverlay(
                result = handResult,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}