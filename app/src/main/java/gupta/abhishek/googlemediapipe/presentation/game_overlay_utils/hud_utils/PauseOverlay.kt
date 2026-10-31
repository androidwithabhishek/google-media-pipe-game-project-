package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import drawMenuIcon
import gupta.abhishek.googlemediapipe.core.fonts.GlossyText
import gupta.abhishek.googlemediapipe.core.game_assets.Game3DSquareButton

import gupta.abhishek.googlemediapipe.core.game_assets.drawPlayIcon
import gupta.abhishek.googlemediapipe.core.game_assets.drawReplayIcon

// ---------------------------------------------------------------------
// PauseOverlay
//   Shown while paused: the screen goes dark, with three buttons.
//   Play   -> continue the game
//   Replay -> start a new game
//   Menu   -> open the menu screen (the game stays paused underneath)
// ---------------------------------------------------------------------
@Composable
internal fun BoxScope.PauseOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,

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
            Game3DSquareButton(
                sizeDp = 110.dp,
                onClick = onResume,
                icon = { r -> drawPlayIcon(r) }
            )

            // Start a new game.
            Game3DSquareButton(
                sizeDp = 110.dp,
                onClick = onRestart,
                icon = { r -> drawReplayIcon(r) }
            )

            // Open the menu screen.
            Game3DSquareButton(
                sizeDp = 110.dp,
                onClick = onMenu,
                icon = { r -> drawMenuIcon(r) }
            )
        }
    }
}