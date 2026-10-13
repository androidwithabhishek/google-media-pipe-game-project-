package gupta.abhishek.googlemediapipe.core.fruit_type

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Orange {

    val type = FruitType(
        name = "orange",
        skinColor = Color(0xFFEF6C00),
        juiceColor = Color(0xFFFFA726),
        insideColor = Color(0xFFFFB74D),
        seedColor = Color(0xFFFFF3E0),
        seedCount = 0, sizeScale = 1f,
        draw = { radius -> drawOrange(radius) }
    )

    // Whole orange, as it looks before it is cut.
    private fun DrawScope.drawOrange(r: Float) {

        val body = Path().apply {
            addOval(Rect(center = Offset.Zero, radius = r))
        }

        // Skin: soft light from the top-left, deeper orange at the edges.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFCC80),
                    Color(0xFFFB8C00),
                    Color(0xFFE65100),
                    Color(0xFFB23B00)
                ),
                center = Offset(-0.35f * r, -0.35f * r),
                radius = 1.6f * r
            ),
            radius = r,
            center = Offset.Zero
        )

        clipPath(body) {
            // Fine peel pores in a golden-angle spiral, dense and small.
            for (i in 0 until 90) {
                val angle = i * 2.39996f
                val distance = r * 0.95f * sqrt((i + 0.5f) / 90f)
                val c = Offset(cos(angle) * distance, sin(angle) * distance)

                drawCircle(
                    color = Color(0xFF8D2C00).copy(alpha = 0.18f),
                    radius = r * 0.02f,
                    center = c
                )
                drawCircle(
                    color = Color(0xFFFFE0B2).copy(alpha = 0.25f),
                    radius = r * 0.008f,
                    center = Offset(c.x + r * 0.015f, c.y - r * 0.015f)
                )
            }

            // Shade toward the bottom-right for roundness.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0xFF3E1500).copy(alpha = 0.4f)),
                    center = Offset(0.3f * r, 0.3f * r),
                    radius = 1.2f * r
                ),
                radius = r,
                center = Offset.Zero
            )

            // Soft gloss on the upper left.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(-0.42f * r, -0.42f * r),
                    radius = 0.35f * r
                ),
                radius = 0.35f * r,
                center = Offset(-0.42f * r, -0.42f * r)
            )
        }

        // Shallow dimple at the top where the fruit was attached.
        drawOval(
            color = Color(0xFF8D2C00).copy(alpha = 0.3f),
            topLeft = Offset(-0.14f * r, -0.9f * r),
            size = Size(0.28f * r, 0.14f * r)
        )

        // Green stem button.
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFAED581), Color(0xFF558B2F)),
                center = Offset(-0.03f * r, -0.97f * r),
                radius = 0.14f * r
            ),
            topLeft = Offset(-0.12f * r, -1.0f * r),
            size = Size(0.24f * r, 0.12f * r)
        )

        // Small leaf beside the stem button.
        val leaf = Path().apply {
            moveTo(0.1f * r, -1.02f * r)
            quadraticBezierTo(0.45f * r, -1.42f * r, 0.85f * r, -1.18f * r)
            quadraticBezierTo(0.48f * r, -0.98f * r, 0.1f * r, -1.02f * r)
            close()
        }
        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                start = Offset(0.1f * r, -1.02f * r),
                end = Offset(0.85f * r, -1.18f * r)
            )
        )

        // Leaf midrib.
        drawLine(
            color = Color(0xFFC8E6C9).copy(alpha = 0.6f),
            start = Offset(0.16f * r, -1.02f * r),
            end = Offset(0.76f * r, -1.16f * r),
            strokeWidth = 0.02f * r,
            cap = StrokeCap.Round
        )
    }

    // Cut orange: peel ring, white pith, juice segments, and a core.
    // Call this from the game when the fruit is shown cut in half.
    fun DrawScope.drawCut(r: Float) {
        val segments = 10
        val step = 2f * PI.toFloat() / segments

        // Peel ring.
        drawCircle(color = Color(0xFFEF6C00), radius = r)

        // White pith.
        drawCircle(color = Color(0xFFFFF3E0), radius = r * 0.9f)

        // Flesh base.
        drawCircle(color = Color(0xFFFFB74D), radius = r * 0.82f)

        // Juice vesicles: small rounded sacs inside each segment.
        for (i in 0 until segments) {
            val base = i * step + step / 2f
            for (k in -1..1) {
                val a = base + k * 0.1f
                val c = Offset(cos(a) * r * 0.46f, sin(a) * r * 0.46f)
                drawCircle(color = Color(0xFFFFA726), radius = r * 0.12f, center = c)
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = r * 0.035f,
                    center = Offset(c.x - r * 0.03f, c.y - r * 0.03f)
                )
            }
        }

        // Segment membranes (pith lines from the center to the rim).
        for (i in 0 until segments) {
            val a = i * step
            drawLine(
                color = Color(0xFFFFF3E0),
                start = Offset.Zero,
                end = Offset(cos(a) * r * 0.82f, sin(a) * r * 0.82f),
                strokeWidth = r * 0.05f,
                cap = StrokeCap.Round
            )
        }

        // Central core.
        drawCircle(color = Color(0xFFFFF3E0), radius = r * 0.08f)
    }
}