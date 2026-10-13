import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType



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

object Pomegranate {

    val type = FruitType(
        name = "pomegranate",
        skinColor = Color(0xFFB51236),
        juiceColor = Color(0xFFC90036),
        insideColor = Color(0xFFFFD6DF),
        seedColor = Color(0xFFE7194F),
        seedCount = 16,
        sizeScale = 1.4f,
        draw = { radius -> drawPomegranate(radius) }
    )

    private fun DrawScope.drawPomegranate(r: Float) {

        // Nearly circular body with subtle natural lobes.
        val body = Path().apply {
            moveTo(0f, -0.88f * r)

            cubicTo(
                0.20f * r, -0.91f * r,
                0.48f * r, -0.82f * r,
                0.68f * r, -0.62f * r
            )

            cubicTo(
                0.89f * r, -0.40f * r,
                0.94f * r, -0.12f * r,
                0.91f * r, 0.17f * r
            )

            cubicTo(
                0.88f * r, 0.50f * r,
                0.63f * r, 0.81f * r,
                0.30f * r, 0.88f * r
            )

            cubicTo(
                0.18f * r, 0.92f * r,
                0.08f * r, 0.89f * r,
                0f, 0.86f * r
            )

            cubicTo(
                -0.08f * r, 0.89f * r,
                -0.18f * r, 0.92f * r,
                -0.30f * r, 0.88f * r
            )

            cubicTo(
                -0.63f * r, 0.81f * r,
                -0.88f * r, 0.50f * r,
                -0.91f * r, 0.17f * r
            )

            cubicTo(
                -0.94f * r, -0.12f * r,
                -0.89f * r, -0.40f * r,
                -0.68f * r, -0.62f * r
            )

            cubicTo(
                -0.48f * r, -0.82f * r,
                -0.20f * r, -0.91f * r,
                0f, -0.88f * r
            )

            close()
        }

        // Rich red peel with a soft central glow.
        drawPath(
            path = body,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF6579),
                    Color(0xFFE52D50),
                    Color(0xFFB20D35),
                    Color(0xFF76051F)
                ),
                center = Offset(-0.34f * r, -0.40f * r),
                radius = 1.65f * r
            )
        )

        clipPath(body) {

            // Tiny peel pores distributed naturally across the fruit.
            for (i in 0 until 100) {
                val angle = i * 2.39996f
                val distance =
                    r * 0.87f * sqrt((i + 0.5f) / 100f)

                val x = cos(angle) * distance
                val y = sin(angle) * distance

                drawCircle(
                    color = Color(0xFF67051B).copy(alpha = 0.17f),
                    radius = r * 0.012f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = Color(0xFFFFB0BA).copy(alpha = 0.32f),
                    radius = r * 0.006f,
                    center = Offset(
                        x - r * 0.012f,
                        y - r * 0.012f
                    )
                )
            }

            // Soft shadow on the lower-right area.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF4B0015).copy(alpha = 0.38f)
                    ),
                    center = Offset(0.42f * r, 0.43f * r),
                    radius = 1.15f * r
                ),
                radius = r,
                center = Offset.Zero
            )

            // Broad glossy reflection on the upper-left.
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.07f),
                        Color.Transparent
                    ),
                    start = Offset(-0.75f * r, -0.72f * r),
                    end = Offset(0.0f, 0.05f * r)
                ),
                topLeft = Offset(-0.76f * r, -0.73f * r),
                size = Size(0.43f * r, 0.85f * r)
            )

            // Small bright reflection.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.30f),
                        Color.White.copy(alpha = 0.09f),
                        Color.Transparent
                    ),
                    center = Offset(-0.43f * r, -0.43f * r),
                    radius = 0.29f * r
                ),
                radius = 0.29f * r,
                center = Offset(-0.43f * r, -0.43f * r)
            )
        }

        // Short, compact crown to preserve the round silhouette.
        val crown = Path().apply {
            moveTo(-0.28f * r, -0.78f * r)

            cubicTo(
                -0.34f * r, -0.86f * r,
                -0.36f * r, -0.98f * r,
                -0.35f * r, -1.01f * r
            )

            lineTo(-0.18f * r, -0.92f * r)
            lineTo(-0.08f * r, -1.08f * r)
            lineTo(0.02f * r, -0.94f * r)
            lineTo(0.20f * r, -1.02f * r)
            lineTo(0.23f * r, -0.88f * r)

            cubicTo(
                0.31f * r, -0.86f * r,
                0.32f * r, -0.80f * r,
                0.28f * r, -0.76f * r
            )

            cubicTo(
                0.10f * r, -0.69f * r,
                -0.10f * r, -0.69f * r,
                -0.28f * r, -0.78f * r
            )

            close()
        }

        drawPath(
            path = crown,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF7184),
                    Color(0xFFD51C43),
                    Color(0xFF850821)
                ),
                start = Offset(-0.25f * r, -1.08f * r),
                end = Offset(0.20f * r, -0.72f * r)
            )
        )

        // Crown ridges.
        drawLine(
            color = Color(0xFFFFA5B0).copy(alpha = 0.65f),
            start = Offset(-0.18f * r, -0.94f * r),
            end = Offset(-0.16f * r, -0.79f * r),
            strokeWidth = 0.018f * r,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color(0xFFFFA5B0).copy(alpha = 0.65f),
            start = Offset(0.02f * r, -1.04f * r),
            end = Offset(0.02f * r, -0.76f * r),
            strokeWidth = 0.018f * r,
            cap = StrokeCap.Round
        )

        // Small indentation at the bottom.
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF68051D).copy(alpha = 0.55f),
                    Color.Transparent
                ),
                center = Offset.Zero,
                radius = 0.16f * r
            ),
            topLeft = Offset(-0.13f * r, 0.80f * r),
            size = Size(0.26f * r, 0.10f * r)
        )
    }
}
