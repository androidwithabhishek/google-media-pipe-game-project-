package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gupta.abhishek.googlemediapipe.R
import gupta.abhishek.googlemediapipe.core.game_assets.drawReplayIcon
import gupta.abhishek.googlemediapipe.core.fonts.GameOverText
import gupta.abhishek.googlemediapipe.core.game_assets.Game3DButton


// ----- Game over card (background picture + text) -----
internal const val GAME_OVER_CARD_WIDTH_DP = 480f       // card width
internal const val GAME_OVER_CARD_HEIGHT_DP = 320f      // card height (match the picture's shape)
internal const val GAME_OVER_CONTENT_PADDING_DP = 40f   // space between the card edge and the text
internal const val GAME_OVER_LINE_GAP_DP = 13f          // space between the text lines
internal const val GAME_OVER_BUTTON_GAP_DP = 10f        // space between the last line and the button
internal const val GAME_OVER_BUTTON_SIZE_DP = 88f       // size of the replay button

// Text sizes (sp)
internal const val GAME_OVER_TITLE_SIZE_SP = 50f        // "GAME OVER" / "NEW HIGH SCORE!"
internal const val GAME_OVER_SCORE_SIZE_SP = 40f        // "Score: ..."
internal const val GAME_OVER_BEST_SIZE_SP = 30f         // "Best: ..."

// Tilt of each line, in degrees (negative = left, positive = right)
internal const val GAME_OVER_TITLE_TILT_DEGREES = -4f
internal const val GAME_OVER_SCORE_TILT_DEGREES = 3f
internal const val GAME_OVER_BEST_TILT_DEGREES = -2f

// Colors (ARGB hex)
internal const val GAME_OVER_NEW_TITLE_COLOR = 0xFFFFD54FL      // gold title on a new high score
internal const val GAME_OVER_NORMAL_TITLE_COLOR = 0xFFFF5252L   // red title on a normal game over
internal const val GAME_OVER_SCORE_COLOR = 0xFFFFFFFFL          // white score
internal const val GAME_OVER_BEST_COLOR = 0xFFFFFF00L           // yellow best score

// ----- Shadows -----
internal const val GAME_OVER_CARD_SHADOW_OFFSET_DP = 10f   // how far the card's shadow sits down-right (dp)
internal const val GAME_OVER_CARD_SHADOW_ALPHA = 0.45f     // darkness of the card shadow (0 = none, 1 = solid black)
internal const val GAME_OVER_TEXT_SHADOW_OFFSET_PX = 5f    // how far the text shadow sits down-right (pixels)
internal const val GAME_OVER_TEXT_SHADOW_BLUR_PX = 4f      // how soft the text shadow is
internal const val GAME_OVER_TEXT_SHADOW_ALPHA = 0.9f      // darkness of the text shadow

@Composable
internal fun BoxScope.GameOverCard(
    currentScore: Int,
    bestScore: Int,
    onRestart: () -> Unit
) {
    val isNewBest = currentScore > 0 && currentScore >= bestScore




//    actual cardPicture
//    val cardPicture = if (isNewBest) R.drawable.game_over_canvas_happy else R.drawable.game_over_canvas_




//  test cardPicture
    val cardPicture = if (isNewBest) R.drawable.game_over_canvas_happy else R.drawable.game_over



    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(
                width = GAME_OVER_CARD_WIDTH_DP.dp,
                height = GAME_OVER_CARD_HEIGHT_DP.dp
            )
    ) {

        // Drop shadow: a black copy of the picture, pushed down-right and drawn first (behind).
        Image(
            painter = painterResource(id = cardPicture),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .offset(x = GAME_OVER_CARD_SHADOW_OFFSET_DP.dp, y = GAME_OVER_CARD_SHADOW_OFFSET_DP.dp),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(Color.Black),
            alpha = GAME_OVER_CARD_SHADOW_ALPHA
        )

        // The card picture fills the whole box.
        Image(
            painter = painterResource(id = cardPicture),
            contentDescription = "gameOverCard",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )

        // The text and the button sit on top of the picture.
        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(GAME_OVER_CONTENT_PADDING_DP.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GameOverText(
                text = if (isNewBest) "NEW HIGH SCORE!" else "GAME OVER",
                fontSize = GAME_OVER_TITLE_SIZE_SP.sp,
                color = if (isNewBest) Color(GAME_OVER_NEW_TITLE_COLOR)
                else Color(GAME_OVER_NORMAL_TITLE_COLOR),
                tiltDegrees = GAME_OVER_TITLE_TILT_DEGREES,
                modifier = Modifier.padding(top = 30.dp)
            )

            Spacer(modifier = Modifier.height(GAME_OVER_LINE_GAP_DP.dp))

            GameOverText(
                text = "Score: $currentScore",
                fontSize = GAME_OVER_SCORE_SIZE_SP.sp,
                color = Color(GAME_OVER_SCORE_COLOR),
                tiltDegrees = GAME_OVER_SCORE_TILT_DEGREES
            )

            Spacer(modifier = Modifier.height(GAME_OVER_LINE_GAP_DP.dp))

            GameOverText(
                text = "Best: $bestScore",
                fontSize = GAME_OVER_BEST_SIZE_SP.sp,
                color = Color(GAME_OVER_BEST_COLOR),
                tiltDegrees = GAME_OVER_BEST_TILT_DEGREES
            )

            Spacer(modifier = Modifier.height(GAME_OVER_BUTTON_GAP_DP.dp))

            Game3DButton(
                sizeDp = GAME_OVER_BUTTON_SIZE_DP.dp,
                onClick = onRestart,
                icon = { r -> drawReplayIcon(r) }
            )
        }
    }
}