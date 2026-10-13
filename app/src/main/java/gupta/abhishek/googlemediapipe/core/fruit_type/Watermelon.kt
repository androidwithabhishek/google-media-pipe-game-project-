package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType

object Watermelon {

    val type = FruitType(
        name = "watermelon",
        skinColor = Color(0xFF2E7D32),
        juiceColor = Color(0xFFFF5252),
        insideColor = Color(0xFFFF6F61),
        seedColor = Color(0xFF212121),
        seedCount = 5, sizeScale = 2f,
        draw = { radius -> drawWatermelon(radius) }
    )

    private fun DrawScope.drawWatermelon(r: Float) {

        // Oval size: wider than it is tall, like a real watermelon.
        val rx = 1.0f * r
        val ry = 0.8f * r

        // Oval shape used as a clip so everything stays inside the melon.
        val body = Path().apply {
            addOval(Rect(left = -rx, top = -ry, right = rx, bottom = ry))
        }

        // Dark green rind with a lighter top-left, like light on the skin.
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF66BB6A),
                    Color(0xFF2E7D32),
                    Color(0xFF1B5E20),
                    Color(0xFF0D3B12)
                ),
                center = Offset(-0.35f * rx, -0.35f * ry),
                radius = 1.6f * r
            ),
            topLeft = Offset(-rx, -ry),
            size = Size(2f * rx, 2f * ry)
        )

        clipPath(body) {
            // Pale rind band just under the skin.
            drawOval(
                color = Color(0xFFC5E1A5).copy(alpha = 0.5f),
                topLeft = Offset(-0.9f * rx, -0.9f * ry),
                size = Size(1.8f * rx, 1.8f * ry),
                style = Stroke(width = 0.08f * r)
            )

            // Curved dark stripes running from the top pole to the bottom pole.
            for (i in -4..4) {
                val x0 = i * 0.2f * rx
                val stripe = Path().apply {
                    moveTo(x0 * 0.5f, -1.1f * ry)
                    quadraticBezierTo(x0 + 0.16f * rx, 0f, x0 * 0.5f, 1.1f * ry)
                }
                drawPath(
                    path = stripe,
                    color = Color(0xFF0B3D12).copy(alpha = 0.75f),
                    style = Stroke(width = 0.1f * r, cap = StrokeCap.Round)
                )
            }

            // Soft shade toward the bottom-right for roundness.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
                    center = Offset(0.25f * rx, 0.25f * ry),
                    radius = 1.3f * r
                ),
                topLeft = Offset(-rx, -ry),
                size = Size(2f * rx, 2f * ry)
            )

            // Glossy highlight on the upper left.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(-0.42f * rx, -0.42f * ry),
                    radius = 0.4f * r
                ),
                topLeft = Offset(-0.82f * rx, -0.82f * ry),
                size = Size(0.8f * r, 0.8f * r)
            )
        }

        // Short curved stem on top.
        val stem = Path().apply {
            moveTo(0f, -0.92f * ry)
            quadraticBezierTo(0.02f * r, -1.1f * ry, 0.12f * r, -1.25f * ry)
        }
        drawPath(
            path = stem,
            color = Color(0xFF5D4037),
            style = Stroke(width = 0.1f * r, cap = StrokeCap.Round)
        )
    }
}