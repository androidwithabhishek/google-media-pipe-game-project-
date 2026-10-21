
package gupta.abhishek.googlemediapipe.core.fruit_type
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Pear {

    val type = FruitType(
        name = "pear",
        skinColor = Color(0xFFC1D86B),
        juiceColor = Color(0xFFF4F7D9),
        insideColor = Color(0xFFFDFCEB),
        seedColor = Color(0xFF3E2712),
        seedCount = 2,
        sizeScale = 1.1f,
        draw = { radius -> drawPear(radius) }
    )

    private fun DrawScope.drawPear(r: Float) {

        // -------------------------------------------------
        // 1. Natural pear silhouette
        // -------------------------------------------------

        val body = Path().apply {
            moveTo(0f, -0.88f * r)

            cubicTo(
                0.12f * r, -0.88f * r,
                0.25f * r, -0.55f * r,
                0.45f * r, -0.05f * r
            )

            cubicTo(
                0.72f * r, 0.40f * r,
                0.48f * r, 0.90f * r,
                0f, 0.90f * r
            )

            cubicTo(
                -0.48f * r, 0.90f * r,
                -0.68f * r, 0.40f * r,
                -0.42f * r, -0.05f * r
            )

            cubicTo(
                -0.23f * r, -0.55f * r,
                -0.12f * r, -0.88f * r,
                0f, -0.88f * r
            )

            close()
        }

        // -------------------------------------------------
        // 2. Soft contact shadow
        // -------------------------------------------------

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF35401C).copy(alpha = 0.20f),
                    Color(0xFF35401C).copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(0f, 0f),
                radius = 1f * r
            ),
            topLeft = Offset(-0.58f * r, 0.69f * r),
            size = Size(1.16f * r, 0.30f * r)
        )

        // -------------------------------------------------
        // 3. Base skin with warm highlights and deep shading
        // -------------------------------------------------

        drawPath(
            path = body,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF2BC),
                    Color(0xFFEAF4A5),
                    Color(0xFFD2E47E),
                    Color(0xFFB1CC5C),
                    Color(0xFF849D3D),
                    Color(0xFF65772D)
                ),
                center = Offset(-0.38f * r, -0.42f * r),
                radius = 1.55f * r
            )
        )

        // -------------------------------------------------
        // 4. Skin texture and subtle natural imperfections
        // -------------------------------------------------

        clipPath(body) {

            for (i in 0 until 420) {

                val angle = i * 2.39996f

                val distance =
                    r * 0.91f * sqrt((i + 0.5f) / 420f)

                val x = cos(angle) * distance
                val y = sin(angle) * distance

                val variation = ((i * 37) % 100) / 100f

                val dotColor = when {
                    variation < 0.15f ->
                        Color(0xFF79552C)

                    variation < 0.40f ->
                        Color(0xFF81923C)

                    variation < 0.72f ->
                        Color(0xFFB4BF65)

                    else ->
                        Color(0xFFFFF1B2)
                }

                val dotRadius = r * (
                        0.0015f + variation * 0.0035f
                        )

                drawCircle(
                    color = dotColor.copy(
                        alpha = 0.08f + variation * 0.15f
                    ),
                    radius = dotRadius,
                    center = Offset(x, y)
                )

                // A few darker pores
                if (i % 11 == 0) {
                    drawCircle(
                        color = Color(0xFF66512A).copy(
                            alpha = 0.22f
                        ),
                        radius = r * 0.003f,
                        center = Offset(x, y)
                    )
                }
            }

            // -------------------------------------------------
            // 5. Directional shading on the lower-right side
            // -------------------------------------------------

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF53652A).copy(alpha = 0.02f),
                        Color(0xFF405020).copy(alpha = 0.18f),
                        Color(0xFF304018).copy(alpha = 0.32f),
                        Color.Transparent
                    ),
                    center = Offset(0.43f * r, 0.48f * r),
                    radius = 0.78f * r
                ),
                topLeft = Offset(-0.10f * r, -0.22f * r),
                size = Size(1.02f * r, 1.10f * r)
            )

            // -------------------------------------------------
            // 6. Broad soft glossy reflection
            // -------------------------------------------------

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.42f),
                        Color(0xFFFFF9D8).copy(alpha = 0.22f),
                        Color(0xFFFFF9D8).copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(-0.38f * r, -0.40f * r),
                    radius = 0.62f * r
                ),
                topLeft = Offset(-0.77f * r, -0.82f * r),
                size = Size(0.78f * r, 1.12f * r)
            )

            // Narrow specular highlight
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.62f),
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(-0.48f * r, -0.70f * r),
                    end = Offset(-0.22f * r, -0.18f * r)
                ),
                topLeft = Offset(-0.48f * r, -0.70f * r),
                size = Size(0.15f * r, 0.45f * r)
            )

            // Secondary reflected light
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFD1).copy(alpha = 0.26f),
                        Color.Transparent
                    ),
                    center = Offset(-0.43f * r, 0.18f * r),
                    radius = 0.40f * r
                ),
                radius = 0.40f * r,
                center = Offset(-0.43f * r, 0.18f * r)
            )

            // Tiny natural marks near the bottom
            for (i in 0 until 12) {
                val x = (-0.30f + i * 0.052f) * r
                val y = (0.76f + (i % 3) * 0.018f) * r

                drawOval(
                    color = Color(0xFF66542B).copy(alpha = 0.16f),
                    topLeft = Offset(x, y),
                    size = Size(0.018f * r, 0.009f * r)
                )
            }
        }

        // -------------------------------------------------
        // 7. Stem shadow
        // -------------------------------------------------

        val stemShadow = Path().apply {
            moveTo(-0.05f * r, -0.85f * r)
            quadraticBezierTo(
                0.07f * r, -0.97f * r,
                0.15f * r, -1.15f * r
            )
        }

        drawPath(
            path = stemShadow,
            color = Color(0xFF3D351D).copy(alpha = 0.30f),
            style = Stroke(
                width = 0.095f * r,
                cap = StrokeCap.Round
            )
        )

        // -------------------------------------------------
        // 8. Main woody stem
        // -------------------------------------------------

        val stem = Path().apply {
            moveTo(-0.05f * r, -0.88f * r)

            quadraticBezierTo(
                0.04f * r, -1.02f * r,
                0.15f * r, -1.15f * r
            )
        }

        drawPath(
            path = stem,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFB98A4D),
                    Color(0xFF91602F),
                    Color(0xFF58371D)
                ),
                start = Offset(-0.05f * r, -0.88f * r),
                end = Offset(0.15f * r, -1.15f * r)
            ),
            style = Stroke(
                width = 0.055f * r,
                cap = StrokeCap.Round
            )
        )

        // Fine stem highlight
        drawPath(
            path = stem,
            color = Color(0xFFE0B77A).copy(alpha = 0.60f),
            style = Stroke(
                width = 0.012f * r,
                cap = StrokeCap.Round
            )
        )

        // -------------------------------------------------
        // 9. Leaf attached to the stem
        // -------------------------------------------------

        val leafAnchor = Offset(0.10f * r, -1.12f * r)

        // Leaf shape
        val leaf = Path().apply {
            moveTo(leafAnchor.x, leafAnchor.y)

            cubicTo(
                -0.10f * r, -1.25f * r,
                -0.40f * r, -1.30f * r,
                -0.55f * r, -1.15f * r
            )

            cubicTo(
                -0.35f * r, -1.05f * r,
                -0.10f * r, -1.02f * r,
                leafAnchor.x, leafAnchor.y
            )

            close()
        }

        // Soft shadow under the leaf
        drawPath(
            path = leaf,
            color = Color(0xFF263D14).copy(alpha = 0.20f)
        )

        // Leaf body
        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFA9CE64),
                    Color(0xFF76A23D),
                    Color(0xFF3D631F),
                    Color(0xFF294819)
                ),
                start = Offset(-0.50f * r, -1.27f * r),
                end = Offset(0.08f * r, -1.03f * r)
            )
        )

        // Leaf shading and veins
        clipPath(leaf) {

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFD9F49A).copy(alpha = 0.48f),
                        Color.Transparent
                    ),
                    center = Offset(-0.38f * r, -1.22f * r),
                    radius = 0.27f * r
                ),
                topLeft = Offset(-0.65f * r, -1.38f * r),
                size = Size(0.60f * r, 0.50f * r)
            )

            // Central vein
            val centralVein = Path().apply {
                moveTo(leafAnchor.x, leafAnchor.y)

                quadraticBezierTo(
                    -0.25f * r, -1.18f * r,
                    -0.50f * r, -1.16f * r
                )
            }

            drawPath(
                path = centralVein,
                color = Color(0xFFC8E58A).copy(alpha = 0.85f),
                style = Stroke(
                    width = 0.018f * r,
                    cap = StrokeCap.Round
                )
            )

            // Side veins
            for (i in 1..4) {
                val t = i / 5f

                val startX =
                    leafAnchor.x +
                            (-0.50f * r - leafAnchor.x) * t

                val startY =
                    leafAnchor.y +
                            (-1.16f * r - leafAnchor.y) * t

                drawPath(
                    path = Path().apply {
                        moveTo(startX, startY)

                        quadraticBezierTo(
                            startX - 0.025f * r,
                            startY - 0.035f * r,
                            startX - 0.10f * r,
                            startY - 0.045f * r
                        )
                    },
                    color = Color(0xFFB9D77A).copy(alpha = 0.65f),
                    style = Stroke(
                        width = 0.010f * r,
                        cap = StrokeCap.Round
                    )
                )

                drawPath(
                    path = Path().apply {
                        moveTo(startX, startY)

                        quadraticBezierTo(
                            startX - 0.025f * r,
                            startY + 0.025f * r,
                            startX - 0.075f * r,
                            startY + 0.045f * r
                        )
                    },
                    color = Color(0xFF28491A).copy(alpha = 0.35f),
                    style = Stroke(
                        width = 0.009f * r,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        // Fine outline on leaf edges
        drawPath(
            path = leaf,
            color = Color(0xFF36551D).copy(alpha = 0.50f),
            style = Stroke(
                width = 0.012f * r
            )
        )
    }
}
