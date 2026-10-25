package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter

// Draws an icon in the given color, centered on (0, 0).
fun DrawScope.drawHandIcon(painter: Painter, r: Float, tint: Color) {
    val iconSize = r * 1.1f
    translate(-iconSize / 2f, -iconSize / 2f) {
        with(painter) {
            draw(size = Size(iconSize, iconSize), colorFilter = ColorFilter.tint(tint))
        }
    }
}