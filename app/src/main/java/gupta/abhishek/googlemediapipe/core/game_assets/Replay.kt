package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.google.ar.core.dependencies.f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ---------------------------------------------------------------------
// drawReplayIcon
//   The round replay arrow, engraved into the 3D yellow button.
//   Paints around (0, 0). "r" is the radius of the BUTTON (not of the arrow).
// ---------------------------------------------------------------------
fun DrawScope.drawReplayIcon(r: Float) {
    engraved(r) { color ->
        drawReplayArrow(r, color)
    }
}

// ---------------------------------------------------------------------
// drawReplayArrow
//   The plain replay arrow in ONE color: a circle that is open at the top,
//   with an arrow head on the end. Both replay buttons use it.
//   "r" is the radius of the button it sits on.
// ---------------------------------------------------------------------
internal fun DrawScope.drawReplayArrow(r: Float, color: Color) {

    // How far the whole icon turns. Positive = clockwise (leans right).
    // Try 15f (a little), 30f (more), or -15f to lean left.
    val ICON_ROTATION_DEGREES = 100f

    // Turn the whole icon around the button center (0, 0).
    rotate(degrees = ICON_ROTATION_DEGREES, pivot = Offset.Zero) {

        val arcRadius = 0.40f * r

        // The open circle, starting at the upper right and going clockwise.
        drawArc(
            color = color,
            startAngle = -50f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(-arcRadius, -arcRadius),
            size = Size(arcRadius * 2f, arcRadius * 2f),
            style = Stroke(width = 0.15f * r, cap = StrokeCap.Round)
        )

        // Where the arc ends (230 degrees, upper left) and the directions around it.
        val endAngle = 230f * (PI.toFloat() / 180f)
        val radialX = cos(endAngle)
        val radialY = sin(endAngle)
        val travelX = -sin(endAngle)
        val travelY = cos(endAngle)
        val endX = radialX * arcRadius
        val endY = radialY * arcRadius

        // The arrow head.
        val head = Path().apply {
            moveTo(endX + travelX * 0.30f * r, endY + travelY * 0.30f * r)
            lineTo(endX + radialX * 0.26f * r, endY + radialY * 0.26f * r)
            lineTo(endX - radialX * 0.26f * r, endY - radialY * 0.26f * r)
            close()
        }
        drawPath(path = head, color = color)

        // Rounded edges on the arrow head.
        drawPath(
            path = head,
            color = color,
            style = Stroke(width = 0.06f * r, join = StrokeJoin.Round)
        )
    }
}