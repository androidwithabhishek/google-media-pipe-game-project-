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
        seedCount = 0, sizeScale = 1.09f,
        draw = { radius -> drawOrange(radius) }
    )

    private fun DrawScope.drawOrange(r: Float) {

        // Round body.
        val body = Path().apply {
            addOval(Rect(center = Offset.Zero, radius = r))
        }

        // Orange skin: lighter top-left, deeper orange at the edges.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFB74D),
                    Color(0xFFFB8C00),
                    Color(0xFFE65100),
                    Color(0xFFBF360C)
                ),
                center = Offset(-0.35f * r, -0.35f * r),
                radius = 1.6f * r
            ),
            radius = r,
            center = Offset.Zero
        )

        clipPath(body) {
            // Fine pores spread in a golden-angle spiral, like real orange peel.
            for (i in 0 until 60) {
                val angle = i * 2.39996f
                val distance = r * 0.92f * sqrt((i + 0.5f) / 60f)
                val center = Offset(cos(angle) * distance, sin(angle) * distance)

                // Dark pore dot.
                drawCircle(
                    color = Color(0xFFBF360C).copy(alpha = 0.22f),
                    radius = r * 0.028f,
                    center = center
                )
                // Light speck beside it for a bumpy look.
                drawCircle(
                    color = Color(0xFFFFCC80).copy(alpha = 0.35f),
                    radius = r * 0.012f,
                    center = Offset(center.x + r * 0.02f, center.y - r * 0.02f)
                )
            }

            // Soft shade toward the bottom-right for roundness.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                    center = Offset(0.25f * r, 0.25f * r),
                    radius = 1.3f * r
                ),
                radius = r,
                center = Offset.Zero
            )

            // Glossy highlight on the upper left.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(-0.42f * r, -0.42f * r),
                    radius = 0.38f * r
                ),
                radius = 0.38f * r,
                center = Offset(-0.42f * r, -0.42f * r)
            )
        }

        // Shallow dimple at the top where the fruit was attached.
        drawOval(
            color = Color(0xFFBF360C).copy(alpha = 0.35f),
            topLeft = Offset(-0.16f * r, -0.9f * r),
            size = Size(0.32f * r, 0.16f * r)
        )

        // Green stem button.
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF9CCC65), Color(0xFF558B2F)),
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
}