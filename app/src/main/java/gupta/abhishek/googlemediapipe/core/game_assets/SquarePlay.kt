package gupta.abhishek.googlemediapipe.core.game_assets



import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// ---------------------------------------------------------------------
// drawStopIcon
//   The stop square, engraved into the 3D yellow button.
//   Paints around (0, 0). "r" is the radius of the BUTTON.
// ---------------------------------------------------------------------
fun DrawScope.drawStopIcon(r: Float) {

    engraved(r) { color ->

        // A square exactly centered on (0, 0). Unlike the triangle it needs
        // no sideways nudge, because a square's visual middle is its real middle.
        val h = 0.26f * r   // half of the side length
        val square = Path().apply {
            moveTo(-h, -h)
            lineTo(h, -h)
            lineTo(h, h)
            lineTo(-h, h)
            close()
        }
        drawPath(path = square, color = color)

        // The same square again as a thick outline with round corners = soft corners.
        drawPath(
            path = square,
            color = color,
            style = Stroke(width = 0.12f * r, join = StrokeJoin.Round)
        )
    }
}