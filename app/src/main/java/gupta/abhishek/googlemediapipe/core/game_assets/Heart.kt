package gupta.abhishek.googlemediapipe.core.game_assets



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate

// ---------------------------------------------------------------------
// Hearts for the lives display. Two kinds, drawn just like the fruits:
//   drawHeart(r)       -> a shiny red heart (a life you still have)
//   drawEmptyHeart(r)  -> a dark empty placeholder (a life you lost)
// Both paint around the point (0, 0), and "r" is the size.
// ---------------------------------------------------------------------

// The heart outline: two round lobes on top, a point at the bottom.
private fun heartPath(r: Float): Path = Path().apply {

    // Start at the point at the bottom.
    moveTo(0f, 0.92f * r)

    // Left side, up to the left lobe.
    cubicTo(
        -0.30f * r, 0.64f * r,
        -1.00f * r, 0.22f * r,
        -1.00f * r, -0.34f * r
    )

    // Over the left lobe, down into the dip in the middle.
    cubicTo(
        -1.00f * r, -0.80f * r,
        -0.45f * r, -1.00f * r,
        0f, -0.55f * r
    )

    // Up and over the right lobe.
    cubicTo(
        0.45f * r, -1.00f * r,
        1.00f * r, -0.80f * r,
        1.00f * r, -0.34f * r
    )

    // Right side, back down to the point.
    cubicTo(
        1.00f * r, 0.22f * r,
        0.30f * r, 0.64f * r,
        0f, 0.92f * r
    )

    close()
}

// ---------------------------------------------------------------------
// drawHeart: the shiny red heart.
// ---------------------------------------------------------------------
fun DrawScope.drawHeart(r: Float) {

    val body = heartPath(r)

    // Soft shadow: the same heart, black and see-through, pushed down and right.
    translate(0.05f * r, 0.09f * r) {
        drawPath(path = body, color = Color.Black.copy(alpha = 0.35f))
    }

    // Red body: bright at the upper left, very dark red at the edges.
    drawPath(
        path = body,
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF8A80),
                Color(0xFFFF1744),
                Color(0xFFC62828),
                Color(0xFF7F0000)
            ),
            center = Offset(-0.3f * r, -0.4f * r),
            radius = 1.7f * r
        )
    )

    // Everything below stays inside the heart shape.
    clipPath(body) {

        // Darker band toward the edges, so the heart looks round.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0xFF7F0000).copy(alpha = 0.45f)),
                center = Offset(0f, -0.05f * r),
                radius = 1.15f * r
            ),
            radius = 1.15f * r,
            center = Offset(0f, -0.05f * r)
        )

        // Warm rim light at the lower right.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(0.55f * r, 0.45f * r),
                radius = 0.5f * r
            ),
            radius = 0.5f * r,
            center = Offset(0.55f * r, 0.45f * r)
        )

        // Big glossy highlight on the left lobe (a tilted white oval that fades downward).
        rotate(degrees = -35f, pivot = Offset(-0.5f * r, -0.55f * r)) {
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.1f)),
                    start = Offset(-0.5f * r, -0.80f * r),
                    end = Offset(-0.5f * r, -0.35f * r)
                ),
                topLeft = Offset(-0.78f * r, -0.82f * r),
                size = Size(0.56f * r, 0.36f * r)
            )
        }

        // Tiny sharp white dot, the brightest spot.
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 0.06f * r,
            center = Offset(-0.62f * r, -0.38f * r)
        )
    }

    // Dark red outline around the whole heart.
    drawPath(
        path = body,
        color = Color(0xFF7F0000),
        style = Stroke(width = 0.07f * r, join = StrokeJoin.Round)
    )
}

// ---------------------------------------------------------------------
// drawEmptyHeart: the empty placeholder for a lost life.
// A dark hollow heart with a gray rim, like a slot waiting to be filled.
// ---------------------------------------------------------------------
fun DrawScope.drawEmptyHeart(r: Float) {

    val body = heartPath(r)

    // Dark see-through inside.
    drawPath(
        path = body,
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF3A2A2A).copy(alpha = 0.85f),
                Color(0xFF150E0E).copy(alpha = 0.9f)
            ),
            center = Offset(0.1f * r, 0.1f * r),
            radius = 1.3f * r
        )
    )

    clipPath(body) {

        // Inner shadow: dark toward the edges, so the heart looks carved into the screen.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                center = Offset(0.1f * r, 0.1f * r),
                radius = 1.1f * r
            ),
            radius = 1.1f * r,
            center = Offset(0.1f * r, 0.1f * r)
        )

        // Faint light on the lower right inside edge.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(0.55f * r, 0.5f * r),
                radius = 0.45f * r
            ),
            radius = 0.45f * r,
            center = Offset(0.55f * r, 0.5f * r)
        )
    }

    // Gray rim around the empty heart.
    drawPath(
        path = body,
        color = Color(0xFF9E8A8A),
        style = Stroke(width = 0.08f * r, join = StrokeJoin.Round)
    )
}