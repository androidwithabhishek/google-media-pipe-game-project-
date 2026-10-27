import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import gupta.abhishek.googlemediapipe.core.game_assets.drawTransparentReplayButton
import gupta.abhishek.googlemediapipe.core.sound.GameSounds
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_MARGIN_END_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_MARGIN_TOP_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_HOLD_SECONDS

@Composable
internal fun BoxScope.RestartButton(
    restartHoldProgress: Float,
    onRestart: () -> Unit,
    sounds: GameSounds? = null
) {
    val holdFraction = if (RESTART_HOLD_SECONDS > 0f)
        (restartHoldProgress / RESTART_HOLD_SECONDS).coerceIn(0f, 1f) else 0f

    // True while a hand is on the button. The sound plays once, when the hand first arrives.
    val isHolding = holdFraction > 0f
    LaunchedEffect(isHolding) {
        if (isHolding) sounds?.handRestartProgressSound()
    }

    Canvas(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = RESTART_BUTTON_MARGIN_TOP_DP.dp, end = RESTART_BUTTON_MARGIN_END_DP.dp)
            .size(RESTART_BUTTON_SIZE_DP.dp)
            .clip(CircleShape)
            .clickable(onClick = onRestart)
    ) {
        val r = size.minDimension / 2f

        translate(size.width / 2f, size.height / 2f) {

            // The glass button with the gold arrow.
            drawTransparentReplayButton(r * 0.96f)

            // Green ring that fills up clockwise while a hand holds on the button.
            if (holdFraction > 0f) {
                drawArc(
                    color = Color(0xFF4CAF50),
                    startAngle = -90f,
                    sweepAngle = 360f * holdFraction,
                    useCenter = false,
                    topLeft = Offset(-r * 0.92f, -r * 0.92f),
                    size = Size(r * 1.84f, r * 1.84f),
                    style = Stroke(width = r * 0.08f, cap = StrokeCap.Round)
                )
            }
        }
    }
}