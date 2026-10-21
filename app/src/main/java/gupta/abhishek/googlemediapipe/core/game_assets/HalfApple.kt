package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

// ---------------------------------------------------------------------
// The apple outline (same shape as the falling apple in Apple.kt):
// wide shoulders and a small dip at the bottom.
// ---------------------------------------------------------------------
private fun applePath(r: Float): Path = Path().apply {
    moveTo(0f, -0.62f * r)
    cubicTo(0.35f * r, -0.95f * r, 1.05f * r, -0.95f * r, 1.0f * r, -0.1f * r)
    cubicTo(0.98f * r, 0.6f * r, 0.6f * r, 1.0f * r, 0.28f * r, 0.9f * r)
    cubicTo(0.12f * r, 0.85f * r, 0.06f * r, 0.72f * r, 0f, 0.78f * r)
    cubicTo(-0.06f * r, 0.72f * r, -0.12f * r, 0.85f * r, -0.28f * r, 0.9f * r)
    cubicTo(-0.6f * r, 1.0f * r, -0.98f * r, 0.6f * r, -1.0f * r, -0.1f * r)
    cubicTo(-1.05f * r, -0.95f * r, -0.35f * r, -0.95f * r, 0f, -0.62f * r)
    close()
}

// Helper to draw an apple seed
private fun DrawScope.drawSeed(r: Float, color: Color) {
    val seedPath = Path().apply {
        moveTo(0f, -0.05f * r)
        cubicTo(0.04f * r, -0.02f * r, 0.04f * r, 0.06f * r, 0f, 0.08f * r)
        cubicTo(-0.04f * r, 0.06f * r, -0.04f * r, -0.02f * r, 0f, -0.05f * r)
        close()
    }
    drawPath(path = seedPath, color = color)
}

// ---------------------------------------------------------------------
// drawHalfApple
//   An apple cut in half, showing the cream colored inside.
//   Used as the small picture next to the score.
//   Paints around (0, 0). "r" is the size.
// ---------------------------------------------------------------------
fun DrawScope.drawHalfApple(r: Float) {

    val body = applePath(r)

    // Soft shadow: the same shape, black and see-through, pushed down and right.
    translate(0.04f * r, 0.07f * r) {
        drawPath(path = body, color = Color.Black.copy(alpha = 0.3f))
    }

    // The red skin. You only see it as a thin edge around the inside.
    drawPath(
        path = body,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFE53935), Color(0xFFB71C1C), Color(0xFF7F0000)),
            center = Offset(-0.3f * r, -0.3f * r),
            radius = 1.5f * r
        )
    )

    // The inside (flesh): the same shape a bit smaller, so a red edge is left around it.
    // Scaled slightly less (0.93f) to make the skin look thinner like the image.
    scale(scale = 0.93f, pivot = Offset.Zero) {
        drawPath(
            path = body,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFDF0), Color(0xFFFFF5D1), Color(0xFFF5DC9A)),
                center = Offset(-0.2f * r, -0.2f * r),
                radius = 1.3f * r
            )
        )
    }

    // The core cavity in the middle
    // 1. Tan oval base
    drawOval(
        color = Color(0xFFEAD7A0),
        topLeft = Offset(-0.16f * r, -0.25f * r),
        size = Size(0.32f * r, 0.60f * r)
    )

    // 2. Slightly darker inner cavity to give depth
    drawOval(
        color = Color(0xFFD8C28A),
        topLeft = Offset(-0.10f * r, -0.18f * r),
        size = Size(0.20f * r, 0.42f * r)
    )

    // 3. Darker outline around the core
    drawOval(
        color = Color(0xFFC8A96A),
        topLeft = Offset(-0.16f * r, -0.25f * r),
        size = Size(0.32f * r, 0.60f * r),
        style = Stroke(width = 0.015f * r)
    )

    // 4. The Seeds (added to match the image)
    val seedColor = Color(0xFF3E2723)

    // Left seed
    translate(-0.07f * r, 0.02f * r) {
        rotate(degrees = -20f) {
            drawSeed(r, seedColor)
        }
    }

    // Right seed
    translate(0.07f * r, 0.02f * r) {
        rotate(degrees = 20f) {
            drawSeed(r, seedColor)
        }
    }

    // Thin line from the top dip down to the core (the core axis line).
    drawLine(
        color = Color(0xFFD9C48A),
        start = Offset(0f, -0.5f * r),
        end = Offset(0f, 0.1f * r), // Extends down through the core
        strokeWidth = 0.025f * r,
        cap = StrokeCap.Round
    )

    // Shiny spot on the inside, upper left.
    drawOval(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(-0.65f * r, -0.45f * r),
        size = Size(0.22f * r, 0.38f * r)
    )

    // Secondary smaller highlight near the bottom right for wetness
    drawOval(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(0.4f * r, 0.35f * r),
        size = Size(0.15f * r, 0.25f * r)
    )

    // Stem (Thicker and darker like the image).
    val stem = Path().apply {
        moveTo(0f, -0.68f * r)
        quadraticBezierTo(0.02f * r, -0.85f * r, 0.10f * r, -1.05f * r)
    }
    drawPath(
        path = stem,
        color = Color(0xFF2E1C15),
        style = Stroke(width = 0.07f * r, cap = StrokeCap.Round)
    )

    // Green leaf (Scaled down and moved closer to the stem).
    val leaf = Path().apply {
        moveTo(0.08f * r, -0.95f * r)
        quadraticBezierTo(0.25f * r, -1.25f * r, 0.55f * r, -1.1f * r)
        quadraticBezierTo(0.3f * r, -0.9f * r, 0.08f * r, -0.95f * r)
        close()
    }
    drawPath(
        path = leaf,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
            start = Offset(0.08f * r, -0.95f * r),
            end = Offset(0.55f * r, -1.1f * r)
        )
    )
}