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
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Mango {

    val type = FruitType(
        name = "mango",
        skinColor = Color(0xFFFBC02D),
        juiceColor = Color(0xFFFFB300),
        insideColor = Color(0xFFFFCA28),
        seedColor = Color(0xFFD7A86E),
        seedCount = 1, sizeScale = 0.9f,
        draw = { radius -> drawMango(radius) }
    )

    private fun DrawScope.drawMango(r: Float) {

        // Mango is an oval, taller than wide, tilted a little.
        val rx = 0.82f * r
        val ry = 1.0f * r

        val body = Path().apply {
            addOval(Rect(left = -rx, top = -ry, right = rx, bottom = ry))
        }

        rotate(degrees = -12f, pivot = Offset.Zero) {

            // Yellow-orange skin, lighter top-left, deeper toward the edges.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF176),
                        Color(0xFFFBC02D),
                        Color(0xFFF9A825),
                        Color(0xFFE65100)
                    ),
                    center = Offset(-0.3f * rx, -0.35f * ry),
                    radius = 1.6f * r
                ),
                topLeft = Offset(-rx, -ry),
                size = Size(2f * rx, 2f * ry)
            )

            clipPath(body) {
                // Red blush on the sun-facing side (right side).
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE53935).copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        center = Offset(0.55f * rx, 0.1f * ry),
                        radius = 0.9f * r
                    ),
                    topLeft = Offset(-rx, -ry),
                    size = Size(2f * rx, 2f * ry)
                )

                // Faint dots spread across the skin.
                for (i in 0 until 40) {
                    val angle = i * 2.39996f
                    val distance = r * 0.85f * sqrt((i + 0.5f) / 40f)
                    drawCircle(
                        color = Color(0xFF7CB342).copy(alpha = 0.25f),
                        radius = r * 0.02f,
                        center = Offset(
                            cos(angle) * distance * 0.82f,
                            sin(angle) * distance
                        )
                    )
                }

                // Soft shade toward the bottom-right for roundness.
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f)),
                        center = Offset(0.25f * rx, 0.25f * ry),
                        radius = 1.3f * r
                    ),
                    topLeft = Offset(-rx, -ry),
                    size = Size(2f * rx, 2f * ry)
                )

                // Glossy highlight on the upper left.
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(-0.38f * rx, -0.42f * ry),
                        radius = 0.36f * r
                    ),
                    topLeft = Offset(-0.74f * rx, -0.78f * ry),
                    size = Size(0.72f * r, 0.72f * r)
                )
            }

            // Short stem at the top.
            val stem = Path().apply {
                moveTo(0f, -0.95f * ry)
                quadraticBezierTo(0.03f * r, -1.08f * ry, 0.1f * r, -1.18f * ry)
            }
            drawPath(
                path = stem,
                color = Color(0xFF5D4037),
                style = Stroke(width = 0.07f * r, cap = StrokeCap.Round)
            )

            // Small leaf near the stem.
            val leaf = Path().apply {
                moveTo(0.04f * r, -1.0f * ry)
                quadraticBezierTo(0.35f * r, -1.3f * ry, 0.6f * r, -1.12f * ry)
                quadraticBezierTo(0.32f * r, -0.95f * ry, 0.04f * r, -1.0f * ry)
                close()
            }
            drawPath(
                path = leaf,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                    start = Offset(0.04f * r, -1.0f * ry),
                    end = Offset(0.6f * r, -1.12f * ry)
                )
            )
        }
    }
}