package gupta.abhishek.googlemediapipe.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandOverlay
import gupta.abhishek.googlemediapipe.presentation.CameraPreview

import gupta.abhishek.googlemediapipe.presentation.MainViewModel

// The real app screen: the camera game with the hand overlay on top.
// Reads the saved best score and saves each finished game's score.
@Composable
fun MainScreen(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    showHandAnimation: Boolean = true,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bestScore by viewModel.bestScore.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            helper = helper,
            handResult = handResult,
            modifier = Modifier.fillMaxSize(),
            bestScoreFromDatabase = bestScore,
            onGameOver = { finalScore -> viewModel.saveScore(finalScore) }
        )

        // Hand animation is drawn only when the flag is true.
        if (showHandAnimation) {
            HandOverlay(
                result = handResult,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}