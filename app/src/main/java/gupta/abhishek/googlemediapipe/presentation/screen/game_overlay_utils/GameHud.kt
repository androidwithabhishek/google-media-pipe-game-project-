package gupta.abhishek.googlemediapipe.presentation.screen.game_overlay_utils

import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.BEST_TEXT_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.HAND_BUTTON_GAP_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.HAND_BUTTON_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.HEART_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.HUD_MARGIN_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.PAUSE_BUTTON_MARGIN_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.PAUSE_BUTTON_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SCORE_ICON_RATIO
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SCORE_TEXT_OFFSET_Y_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SCORE_TEXT_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SHOW_HAND_HINT
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SHOW_HEART_BACKGROUND
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.STARTING_LIVES



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.R
import gupta.abhishek.googlemediapipe.core.game_assets.drawEmptyHeart
import gupta.abhishek.googlemediapipe.core.game_assets.cameraIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawHeart
import gupta.abhishek.googlemediapipe.core.game_assets.drawPauseIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawPlayIcon


@Composable
internal fun BoxScope.GameHud(
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
                        cameraIcon(handOnPainter, r, tint = Color(0xFF3E2723))   // dark brown: camera on
                    } else {
                        cameraIcon(handOffPainter, r, tint = Color(0xFFB71C1C))  // dark red: camera off
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

@Composable
private fun BoxScope.ScoreCorner(currentScore: Int, bestScore: Int) {

    Column(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = HUD_MARGIN_DP.dp, top = HUD_MARGIN_DP.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // The apple's size follows the score text, so the two stay in proportion.
            val appleSize = with(LocalDensity.current) {
                SCORE_TEXT_SIZE_SP.sp.toDp() * SCORE_ICON_RATIO
            }

            Image(
                painter = painterResource(id = R.drawable.half_apple),
                contentDescription = "Score",
                modifier = Modifier.size(appleSize),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(5.dp))

            GlossyText(
                text = "$currentScore",
                fontSize = SCORE_TEXT_SIZE_SP.sp,
                color = Color(0xFF69F0AE),
                modifier = Modifier.offset(y = SCORE_TEXT_OFFSET_Y_DP.dp)
            )
        }

        GlossyText(
            text = "Best: $bestScore",
            fontSize = BEST_TEXT_SIZE_SP.sp,
            color = Color(0xFFFFD54F)
        )
    }
}


@Composable
private fun BoxScope.LivesBar(livesLeft: Int, handResult: HandLandmarkerResult?) {

    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = HUD_MARGIN_DP.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .then(
                    if (SHOW_HEART_BACKGROUND)
                        Modifier.background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (lifeNumber in 0 until STARTING_LIVES) {

                // The first "livesLeft" hearts are full, the rest are empty.
                val isFull = lifeNumber < livesLeft

                Canvas(modifier = Modifier.size(HEART_SIZE_DP.dp)) {
                    translate(size.width / 2f, size.height / 2f) {
                        val heartRadius = size.minDimension * 0.46f
                        if (isFull) drawHeart(heartRadius) else drawEmptyHeart(heartRadius)
                    }
                }
            }
        }

        // Small hint, only while the camera does not see a hand.
        val handIsVisible = handResult?.landmarks()?.isEmpty() == false
        if (SHOW_HAND_HINT && !handIsVisible) {
            Text(
                text = "Show your hand",
                color = Color(0xFFFF5252),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}