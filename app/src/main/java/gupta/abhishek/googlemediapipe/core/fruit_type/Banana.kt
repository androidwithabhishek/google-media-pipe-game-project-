
package gupta.abhishek.googlemediapipe.core.fruit_type

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.core.fruits.FruitType

object Banana {

    val type = FruitType(
        name = "banana",
        skinColor = Color(0xFFFFEB3B),
        juiceColor = Color(0xFFFFF8E1),
        insideColor = Color(0xFFFFF9C4),
        seedColor = Color(0xFFFFF9C4),
        seedCount = 0,
        sizeScale = 1.1f,
        draw = { radius -> drawBanana(radius) }
    )

    private fun DrawScope.drawBanana(r: Float) {

        rotate(degrees = -20f, pivot = Offset.Zero) {

            // Thick outer body with a narrower inner curve.
            val body = Path().apply {
                moveTo(-0.95f * r, -0.55f * r)

                cubicTo(
                    -0.75f * r, 1.0f * r,
                    0.75f * r, 1.0f * r,
                    0.95f * r, -0.55f * r
                )

                quadraticBezierTo(
                    1.02f * r, -0.66f * r,
                    0.92f * r, -0.45f * r
                )

                // Reduced inner curve depth.
                cubicTo(
                    0.5f * r, -0.02f * r,
                    -0.5f * r, -0.02f * r,
                    -0.92f * r, -0.45f * r
                )

                quadraticBezierTo(
                    -1.0f * r, -0.62f * r,
                    -0.95f * r, -0.55f * r
                )

                close()
            }

            // Yellow skin gradient.
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

            clipPath(body) {

                // Thin shadow following the smaller inner curve.
                val innerShade = Path().apply {
                    moveTo(0.9f * r, -0.45f * r)

                    cubicTo(
                        0.5f * r, -0.02f * r,
                        -0.5f * r, -0.02f * r,
                        -0.92f * r, -0.45f * r
                    )
                }

                drawPath(
                    path = innerShade,
                    color = Color(0xFF9E9D24).copy(alpha = 0.25f),
                    style = Stroke(
                        width = 0.12f * r,
                        cap = StrokeCap.Round
                    )
                )

                // Subtle peel ridges.
                for (f in listOf(0.35f, 0.7f)) {
                    val ridge = Path().apply {
                        val yMid = (0.4f + (0.9f - 0.4f) * f) * r

                        moveTo(-0.85f * r, -0.5f * r)

                        cubicTo(
                            -0.5f * r, yMid,
                            0.5f * r, yMid,
                            0.85f * r, -0.5f * r
                        )
                    }

                    drawPath(
                        path = ridge,
                        color = Color(0xFFAFB42B).copy(alpha = 0.22f),
                        style = Stroke(
                            width = 0.022f * r,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Small natural skin spots.
                val spots = listOf(
                    Offset(-0.35f, 0.36f),
                    Offset(0.3f, 0.34f),
                    Offset(0.5f, 0.12f)
                )

                for (spot in spots) {
                    drawCircle(
                        color = Color(0xFF8D6E00).copy(alpha = 0.2f),
                        radius = 0.018f * r,
                        center = Offset(
                            spot.x * r,
                            spot.y * r
                        )
                    )
                }

                // Glossy highlight.
                val shine = Path().apply {
                    moveTo(-0.6f * r, 0f)

                    quadraticBezierTo(
                        0f,
                        0.38f * r,
                        0.55f * r,
                        -0.05f * r
                    )
                }

                drawPath(
                    path = shine,
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(
                        width = 0.08f * r,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Thin outer outline.
            drawPath(
                path = body,
                color = Color(0xFFC0CA33).copy(alpha = 0.45f),
                style = Stroke(width = 0.022f * r)
            )

            // Green stalk at the right end.
            val neck = Path().apply {
                moveTo(0.9f * r, -0.5f * r)

                quadraticBezierTo(
                    1.05f * r, -0.6f * r,
                    1.16f * r, -0.72f * r
                )
            }

            drawPath(
                path = neck,
                color = Color(0xFF7CB342),
                style = Stroke(
                    width = 0.13f * r,
                    cap = StrokeCap.Round
                )
            )

            drawCircle(
                color = Color(0xFF558B2F),
                radius = 0.06f * r,
                center = Offset(1.16f * r, -0.72f * r)
            )

            // Dark blossom tip.
            drawCircle(
                color = Color(0xFF4E342E),
                radius = 0.05f * r,
                center = Offset(-0.93f * r, -0.52f * r)
            )
        }
    }
}
