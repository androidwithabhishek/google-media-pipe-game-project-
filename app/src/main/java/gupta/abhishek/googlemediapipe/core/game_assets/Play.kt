package gupta.abhishek.googlemediapipe.core.game_assets


import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// ---------------------------------------------------------------------
// drawPlayIcon
//   The play triangle, engraved into the 3D yellow button.
//   Paints around (0, 0). "r" is the radius of the BUTTON.
// ---------------------------------------------------------------------
fun DrawScope.drawPlayIcon(r: Float) {

    engraved(r) { color ->

        // A triangle pointing right. It sits a little left of center,
        // because the visual middle of a triangle is nearer its flat side.
        val triangle = Path().apply {
            moveTo(-0.20f * r, -0.38f * r)
            lineTo(0.40f * r, 0f)
            lineTo(-0.20f * r, 0.38f * r)
            close()
        }
        drawPath(path = triangle, color = color)

        // The same triangle again as a thick outline with round corners = soft corners.
        drawPath(
            path = triangle,
            color = color,
            style = Stroke(width = 0.12f * r, join = StrokeJoin.Round)
        )
    }
}
