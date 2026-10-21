package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate

// ---------------------------------------------------------------------
// drawTransparentReplayButton
//   The big replay button for the top right corner (game over).
//   It looks different from the 3D yellow buttons: it is see-through glass,
//   with a glowing gold ring and a big gold replay arrow.
//   Paints around (0, 0). "r" is the radius of the button.
// ---------------------------------------------------------------------
fun DrawScope.drawTransparentReplayButton(r: Float) {

    // Dark glass disc. You can still see the game through it.
    drawCircle(
        color = Color.Black.copy(alpha = 0.28f),
        radius = r,
        center = Offset.Zero
    )

    // Soft glow just inside the ring.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xFFFFC107).copy(alpha = 0.25f)),
            center = Offset.Zero,
            radius = r
        ),
        radius = r,
        center = Offset.Zero
    )

    // Gold ring: light at the top, deeper orange at the bottom.
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF59D), Color(0xFFFFC400), Color(0xFFFF9800)),
            start = Offset(0f, -r),
            end = Offset(0f, r)
        ),
        radius = r * 0.96f,
        center = Offset.Zero,
        style = Stroke(width = r * 0.08f)
    )

    // A short bright arc on the ring, upper left, like light on glass.
    drawArc(
        color = Color.White.copy(alpha = 0.55f),
        startAngle = 200f,
        sweepAngle = 70f,
        useCenter = false,
        topLeft = Offset(-r * 0.86f, -r * 0.86f),
        size = Size(r * 1.72f, r * 1.72f),
        style = Stroke(width = r * 0.05f, cap = StrokeCap.Round)
    )

    // The replay arrow, drawn bigger than on the small buttons.
    val arrowSize = r * 1.3f

    // Dark shadow copy, a little lower, so the arrow stands out from the game picture.
    translate(0.02f * r, 0.05f * r) {
        drawReplayArrow(arrowSize, Color.Black.copy(alpha = 0.45f))
    }

    // The gold arrow on top.
    drawReplayArrow(arrowSize, Color(0xFFFFC107))
}
