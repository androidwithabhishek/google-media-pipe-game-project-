package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.game_assets.drawEmptyHeart
import gupta.abhishek.googlemediapipe.core.game_assets.drawHeart
import gupta.abhishek.googlemediapipe.presentation.game_logic.HEART_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.HUD_MARGIN_DP
import gupta.abhishek.googlemediapipe.presentation.game_logic.SHOW_HAND_HINT
import gupta.abhishek.googlemediapipe.presentation.game_logic.SHOW_HEART_BACKGROUND
import gupta.abhishek.googlemediapipe.presentation.game_logic.STARTING_LIVES

@Composable
 fun BoxScope.LivesBar(livesLeft: Int, handResult: HandLandmarkerResult?) {

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