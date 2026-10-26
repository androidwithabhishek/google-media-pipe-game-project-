package gupta.abhishek.googlemediapipe.presentation.screen.game_overlay_utils

import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_BEST_COLOR
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_BEST_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_BEST_TILT_DEGREES
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_BUTTON_GAP_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_BUTTON_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_CARD_HEIGHT_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_CARD_WIDTH_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_CONTENT_PADDING_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_LINE_GAP_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_NEW_TITLE_COLOR
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_NORMAL_TITLE_COLOR
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_SCORE_COLOR
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_SCORE_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_SCORE_TILT_DEGREES
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_TITLE_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GAME_OVER_TITLE_TILT_DEGREES



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gupta.abhishek.googlemediapipe.R
import gupta.abhishek.googlemediapipe.core.game_assets.drawReplayIcon

// ---------------------------------------------------------------------
// GameOverCard
//   Center card on game over. The picture depends on the result:
//     new high score   -> game_over_canvas_happy.png
//     normal game over -> game_over_canvas.png
//   All sizes, tilts and colors are in GameSettings.kt (GAME_OVER_*).
// ---------------------------------------------------------------------
@Composable
internal fun BoxScope.GameOverCard(
    currentScore: Int,
    bestScore: Int,
    onRestart: () -> Unit
) {
    val isNewBest = currentScore > 0 && currentScore >= bestScore
    val cardPicture = if (isNewBest) R.drawable.game_over_canvas_happy else R.drawable.game_over_canvas_

    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(
                width = GAME_OVER_CARD_WIDTH_DP.dp,
                height = GAME_OVER_CARD_HEIGHT_DP.dp
            )
    ) {

        // The card picture fills the whole box.
        Image(
            painter = painterResource(id = cardPicture),
            contentDescription = null,
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
                tiltDegrees = GAME_OVER_TITLE_TILT_DEGREES
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