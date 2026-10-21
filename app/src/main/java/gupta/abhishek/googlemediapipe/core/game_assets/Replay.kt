package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

    val arcRadius = 0.40f * r

    // The open circle. Angles: 0 = right, 90 = down, so -50 is the upper right.
    // It starts at -50 degrees and goes 280 degrees clockwise, ending at 230 (upper left).
    drawArc(
        color = color,
        startAngle = -50f,
        sweepAngle = 280f,
        useCenter = false,
        topLeft = Offset(-arcRadius, -arcRadius),
        size = Size(arcRadius * 2f, arcRadius * 2f),
        style = Stroke(width = 0.15f * r, cap = StrokeCap.Round)
    )

    // The arrow head sits on the end of the arc (230 degrees).
    val endAngle = 230f * (PI.toFloat() / 180f)

    // Direction pointing away from the circle center (radial)...
    val radialX = cos(endAngle)
    val radialY = sin(endAngle)

    // ...and the direction the arc is travelling (clockwise = 90 degrees turned).
    val travelX = -sin(endAngle)
    val travelY = cos(endAngle)

    // The exact point where the arc ends.
    val endX = radialX * arcRadius
    val endY = radialY * arcRadius

    // A triangle: the tip is ahead of the arc end, the two back corners are on both sides of it.
    val head = Path().apply {
        moveTo(endX + travelX * 0.30f * r, endY + travelY * 0.30f * r)
        lineTo(endX + radialX * 0.26f * r, endY + radialY * 0.26f * r)
        lineTo(endX - radialX * 0.26f * r, endY - radialY * 0.26f * r)
        close()
    }
    drawPath(path = head, color = color)

    // Same triangle again as a thick outline with round corners = softly rounded arrow head.
    drawPath(
        path = head,
        color = color,
        style = Stroke(width = 0.06f * r, join = StrokeJoin.Round)
    )
}
