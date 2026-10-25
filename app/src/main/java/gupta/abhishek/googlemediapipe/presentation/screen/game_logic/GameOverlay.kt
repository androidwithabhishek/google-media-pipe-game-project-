package gupta.abhishek.googlemediapipe.presentation.screen.game_logic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DoNotTouch
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.game_assets.drawEmptyHeart

import gupta.abhishek.googlemediapipe.core.game_assets.drawHandIcon

import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import gupta.abhishek.googlemediapipe.R
import gupta.abhishek.googlemediapipe.core.fonts.GameFonts


import gupta.abhishek.googlemediapipe.core.game_assets.drawHeart
import gupta.abhishek.googlemediapipe.core.game_assets.drawPauseIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawPlayIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawReplayIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawTransparentReplayButton
import gupta.abhishek.googlemediapipe.core.game_assets.drawYellowRoundButton

// ---------------------------------------------------------------------
// GlossyText
//   Big bold text with a dark shadow, like the score text in the reference picture.
// ---------------------------------------------------------------------
@Composable
private fun GlossyText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = GameFonts.JungleRoarFont,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = fontSize,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.75f),
                offset = Offset(3f, 4f),
                blurRadius = 6f
            ),
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    )
}

@Composable
internal fun Game3DButton(
    sizeDp: Dp,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onClick: () -> Unit,
    icon: DrawScope.(radius: Float) -> Unit
) {
    Canvas(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        // The button is a bit smaller than the box, so its shadow still fits inside.
        val r = size.minDimension / 2f * 0.88f

        translate(size.width / 2f, size.height / 2f) {

            // The yellow 3D button, then the icon on it.
            drawYellowRoundButton(r)
            icon(r)

            // Green ring that fills up clockwise while a hand holds on the button.
            if (progress > 0f) {
                drawArc(
                    color = Color(0xFF4CAF50),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(-r * 0.94f, -r * 0.94f),
                    size = Size(r * 1.88f, r * 1.88f),
                    style = Stroke(width = r * 0.14f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// GameHud
//   Everything you see on top of the game while playing:
//     top left      -> fruit picture + Score, with Best under it
//     top center    -> hearts (one per life)
//     bottom left   -> hand button (on/off) with the pause button under it
// ---------------------------------------------------------------------
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

    if (showPauseButton) {
        // Bottom left column: hand button on top, pause button under it.
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
                // Red slash when hand control is off (camera off, touch only).
                icon = { r ->
                    if (handControlEnabled) {
                        drawHandIcon(handOnPainter, r, tint = Color(0xFF3E2723))   // dark brown: hand on
                    } else {
                        drawHandIcon(handOffPainter, r, tint = Color(0xFFB71C1C))  // dark red: hand off
                    }
                }
            )

            Game3DButton(
                sizeDp = PAUSE_BUTTON_SIZE_DP.dp,
                onClick = onPauseToggle,
                // While paused the same button shows Play, so you can tap it to continue.
                icon = { r -> if (isPaused) drawPlayIcon(r) else drawPauseIcon(r) }
            )
        }
    }
}

// ---------------------------------------------------------------------
// ScoreCorner
//   Top left: half an apple, the score in big letters, and "Best" under it.
// ---------------------------------------------------------------------
@Composable
private fun BoxScope.ScoreCorner(currentScore: Int, bestScore: Int) {

    Column(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = HUD_MARGIN_DP.dp, top = HUD_MARGIN_DP.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

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
                text = "\u270B Show your hand",
                color = Color(0xFFFF5252),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}


@Composable
internal fun BoxScope.PauseOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GlossyText(text = "PAUSED", fontSize = 48.sp, color = Color(0xFFFFD54F))

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {

            // Continue the game.
            Game3DButton(
                sizeDp = 110.dp,
                onClick = onResume,
                icon = { r -> drawPlayIcon(r) }
            )

            // Start a new game.
            Game3DButton(
                sizeDp = 110.dp,
                onClick = onRestart,
                icon = { r -> drawReplayIcon(r) }
            )
        }
    }
}

@Composable
internal fun BoxScope.RestartButton(
    restartHoldProgress: Float,
    onRestart: () -> Unit
) {
    val holdFraction = if (RESTART_HOLD_SECONDS > 0f)
        (restartHoldProgress / RESTART_HOLD_SECONDS).coerceIn(0f, 1f) else 0f

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

// ---------------------------------------------------------------------
// GameOverCard
//   Center card shown on game over: score, best score and the replay button.
// ---------------------------------------------------------------------
@Composable
internal fun BoxScope.GameOverCard(
    currentScore: Int,
    bestScore: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .background(
                Color(0xFF1B1B2F).copy(alpha = 0.92f),
                RoundedCornerShape(28.dp)
            )
            .border(
                width = 3.dp,
                color = Color(0xFFFF5252),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 48.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)   // same gap between every line
    ) {

        GlossyText(
            text = "\uD83D\uDC80 GAME OVER",
            fontSize = 36.sp,
            color = Color(0xFFFF5252)
        )

        GlossyText(
            text = "Score: $currentScore",
            fontSize = 32.sp,
            color = Color.White
        )

        val isNewBest = currentScore > 0 && currentScore >= bestScore
        GlossyText(
            text = if (isNewBest) "\uD83D\uDE0E New high score!"
            else "\uD83C\uDFC6 Best: $bestScore",
            fontSize = 26.sp,
            color = Color.Yellow
        )

        Spacer(modifier = Modifier.height(4.dp))

        Game3DButton(
            sizeDp = 88.dp,
            onClick = onRestart,
            icon = { r -> drawReplayIcon(r) }
        )
    }
}