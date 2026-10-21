package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

// ---------------------------------------------------------------------
// drawPauseIcon
//   Two bars, engraved into the 3D yellow button.
//   Paints around (0, 0). "r" is the radius of the BUTTON.
// ---------------------------------------------------------------------
fun DrawScope.drawPauseIcon(r: Float) {

    engraved(r) { color ->

        val barWidth = 0.20f * r
        val barHeight = 0.62f * r
        val corner = CornerRadius(0.07f * r)

        // Left bar.
        drawRoundRect(
            color = color,
            topLeft = Offset(-0.30f * r, -barHeight / 2f),
            size = Size(barWidth, barHeight),
            cornerRadius = corner
        )

        // Right bar.
        drawRoundRect(
            color = color,
            topLeft = Offset(0.10f * r, -barHeight / 2f),
            size = Size(barWidth, barHeight),
            cornerRadius = corner
        )
    }
}
