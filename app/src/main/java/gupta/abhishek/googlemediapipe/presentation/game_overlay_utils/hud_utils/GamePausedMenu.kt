package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils


import gupta.abhishek.googlemediapipe.presentation.game_logic.HAND_BUTTON_GAP_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.HAND_BUTTON_SIZE_DP

import gupta.abhishek.googlemediapipe.presentation.game_logic.PAUSE_BUTTON_MARGIN_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.PAUSE_BUTTON_SIZE_DP
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.NoPhotography

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.rememberVectorPainter

import androidx.compose.ui.unit.dp

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.game_assets.Game3DButton

import gupta.abhishek.googlemediapipe.core.game_assets.cameraIcon

import gupta.abhishek.googlemediapipe.core.game_assets.drawPauseIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawPlayIcon


@Composable
internal fun BoxScope.GamePausedMenu(
    currentScore: Int,
    livesLeft: Int,
    bestScore: Int,
    handResult: HandLandmarkerResult?,
    isPaused: Boolean,
    showPauseButton: Boolean,
    onPauseToggle: () -> Unit,
    handControlEnabled: Boolean,
    onHandToggle: () -> Unit
) {
    val handOnPainter = rememberVectorPainter(Icons.Filled.CameraAlt)
    val handOffPainter = rememberVectorPainter(Icons.Filled.NoPhotography)

    ScoreCorner(currentScore = currentScore, bestScore = bestScore)

    LivesBar(livesLeft = livesLeft, handResult = handResult)

    if (showPauseButton && !isPaused) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = PAUSE_BUTTON_MARGIN_DP.dp, bottom = PAUSE_BUTTON_MARGIN_DP.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HAND_BUTTON_GAP_DP.dp)
        ) {
            Game3DButton(
                sizeDp = HAND_BUTTON_SIZE_DP.dp,
                onClick = onHandToggle,
                icon = { r ->
                    if (handControlEnabled) {
                        cameraIcon(
                            handOnPainter,
                            r,
                            tint = Color(0xFF3E2723)
                        )   // dark brown: camera on
                    } else {
                        cameraIcon(
                            handOffPainter,
                            r,
                            tint = Color(0xFFB71C1C)
                        )  // dark red: camera off
                    }
                }
            )

            Game3DButton(
                sizeDp = PAUSE_BUTTON_SIZE_DP.dp,
                onClick = onPauseToggle,
                // While paused the same button shows Play, so tapping it continues the game.
                icon = { r -> if (isPaused) drawPlayIcon(r) else drawPauseIcon(r) }
            )
        }
    }
}



