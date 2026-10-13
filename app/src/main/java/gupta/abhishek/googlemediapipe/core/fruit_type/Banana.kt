package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType

object Banana {

    val type = FruitType(
        name = "banana",
        skinColor = Color(0xFFFFEB3B),
        // Pale, creamy juice (banana juice is light, not bright)
        juiceColor = Color(0xFFFFF8E1),
        insideColor = Color(0xFFFFF9C4),
        seedColor = Color(0xFFFFF9C4),
        seedCount = 0, sizeScale = 1.08f,
        draw = { radius -> drawBanana(radius) }
    )

    private fun DrawScope.drawBanana(r: Float) {

        // The banana is a thicker crescent, tilted a little for a natural pose.
        rotate(degrees = -20f, pivot = Offset.Zero) {

            // Plump body: a deep convex back, rounded tip, and a fuller inner curve.
            val body = Path().apply {
                moveTo(-0.95f * r, -0.55f * r)
                cubicTo(-0.75f * r, 1.0f * r, 0.75f * r, 1.0f * r, 0.95f * r, -0.55f * r)
                quadraticBezierTo(1.02f * r, -0.66f * r, 0.92f * r, -0.45f * r)
                cubicTo(0.5f * r, 0.28f * r, -0.5f * r, 0.28f * r, -0.92f * r, -0.45f * r)
                quadraticBezierTo(-1.0f * r, -0.62f * r, -0.95f * r, -0.55f * r)
                close()
            }

            // Bright, fresh yellow with a slight green tint at the ends.
            drawPath(
                path = body,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4),
                        Color(0xFFFFEE58),
                        Color(0xFFFDD835),
                        Color(0xFFCDDC39)
                    ),
                    start = Offset(0f, -0.5f * r),
                    end = Offset(0f, 0.9f * r)
                )
            )

            // Everything below stays inside the banana shape.
            clipPath(body) {
                // Soft shade along the inner curve for roundness.
                val innerShade = Path().apply {
                    moveTo(0.9f * r, -0.45f * r)
                    cubicTo(0.5f * r, 0.28f * r, -0.5f * r, 0.28f * r, -0.92f * r, -0.45f * r)
                }
                drawPath(
                    path = innerShade,
                    color = Color(0xFF9E9D24).copy(alpha = 0.25f),
                    style = Stroke(width = 0.22f * r, cap = StrokeCap.Round)
                )

                // Faint ridge lines along the peel, following the plump curve.
                for (f in listOf(0.35f, 0.7f)) {
                    val ridge = Path().apply {
                        val yMid = (0.4f + (0.9f - 0.4f) * f) * r
                        moveTo(-0.85f * r, -0.5f * r)
                        cubicTo(-0.5f * r, yMid, 0.5f * r, yMid, 0.85f * r, -0.5f * r)
                    }
                    drawPath(
                        path = ridge,
                        color = Color(0xFFAFB42B).copy(alpha = 0.22f),
                        style = Stroke(width = 0.022f * r, cap = StrokeCap.Round)
                    )
                }

                // A few small, light sugar spots.
                val spots = listOf(
                    Offset(-0.35f, 0.36f), Offset(0.3f, 0.34f), Offset(0.5f, 0.12f)
                )
                for (s in spots) {
                    drawCircle(
                        color = Color(0xFF8D6E00).copy(alpha = 0.2f),
                        radius = 0.018f * r,
                        center = Offset(s.x * r, s.y * r)
                    )
                }

                // Bright highlight along the lit upper part of the peel.
                val shine = Path().apply {
                    moveTo(-0.6f * r, 0.0f * r)
                    quadraticBezierTo(0f, 0.38f * r, 0.55f * r, -0.05f * r)
                }
                drawPath(
                    path = shine,
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(width = 0.08f * r, cap = StrokeCap.Round)
                )
            }

            // Thin, light outline for a clean edge.
            drawPath(
                path = body,
                color = Color(0xFFC0CA33).copy(alpha = 0.45f),
                style = Stroke(width = 0.022f * r)
            )

            // Green stalk at the right end, then a small brown tip.
            val neck = Path().apply {
                moveTo(0.9f * r, -0.5f * r)
                quadraticBezierTo(1.05f * r, -0.6f * r, 1.16f * r, -0.72f * r)
            }
            drawPath(
                path = neck,
                color = Color(0xFF7CB342),
                style = Stroke(width = 0.13f * r, cap = StrokeCap.Round)
            )
            drawCircle(
                color = Color(0xFF558B2F),
                radius = 0.06f * r,
                center = Offset(1.16f * r, -0.72f * r)
            )

            // Small dark blossom tip at the left end.
            drawCircle(
                color = Color(0xFF4E342E),
                radius = 0.05f * r,
                center = Offset(-0.93f * r, -0.52f * r)
            )
        }
    }
}