package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gupta.abhishek.googlemediapipe.presentation.game_logic.BEST_TEXT_SIZE_SP
import gupta.abhishek.googlemediapipe.presentation.game_logic.HUD_MARGIN_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.SCORE_ICON_RATIO
import gupta.abhishek.googlemediapipe.presentation.game_logic.SCORE_TEXT_OFFSET_Y_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.SCORE_TEXT_SIZE_SP
import gupta.abhishek.googlemediapipe.core.fonts.GlossyText



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import gupta.abhishek.googlemediapipe.R


@Composable
fun BoxScope.ScoreCorner(currentScore: Int, bestScore: Int) {

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
