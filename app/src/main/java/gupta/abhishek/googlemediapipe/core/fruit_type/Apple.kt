package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.core.fruits.FruitType

object Apple {

    val type = FruitType(
        name = "apple",
        skinColor = Color(0xFFD32F2F),
        juiceColor = Color(0xC9FFE3E3),
        insideColor = Color(0xFFFFD6DF),
        seedColor = Color(0xFF4E342E),
        seedCount = 5,
        sizeScale = 1f,
        draw = { radius -> drawApple(radius) }
    )

    private fun DrawScope.drawApple(r: Float) {

        // Apple body: wide shoulders and a natural dip at the base.
        val body = Path().apply {
            moveTo(0f, -0.62f * r)

            cubicTo(
                0.35f * r, -0.95f * r,
                1.05f * r, -0.95f * r,
                1.0f * r, -0.1f * r
            )

            cubicTo(
                0.98f * r, 0.6f * r,
                0.6f * r, 1.0f * r,
                0.28f * r, 0.9f * r
            )

            cubicTo(
                0.12f * r, 0.85f * r,
                0.06f * r, 0.72f * r,
                0f, 0.78f * r
            )

            cubicTo(
                -0.06f * r, 0.72f * r,
                -0.12f * r, 0.85f * r,
                -0.28f * r, 0.9f * r
            )

            cubicTo(
                -0.6f * r, 1.0f * r,
                -0.98f * r, 0.6f * r,
                -1.0f * r, -0.1f * r
            )

            cubicTo(
                -1.05f * r, -0.95f * r,
                -0.35f * r, -0.95f * r,
                0f, -0.62f * r
            )

            close()
        }

        // Deep red apple skin.
        drawPath(
            path = body,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF6F60),
                    Color(0xFFE53935),
                    Color(0xFFB71C1C),
                    Color(0xFF7F0000)
                ),
                center = Offset(-0.35f * r, -0.35f * r),
                radius = 1.5f * r
            )
        )

        clipPath(body) {

            // Warm rim light on the bottom-right.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF8A80).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(0.6f * r, 0.6f * r),
                    radius = 0.5f * r
                ),
                radius = 0.5f * r,
                center = Offset(0.6f * r, 0.6f * r)
            )

            // Darker band for depth.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF7F0000).copy(alpha = 0.35f)
                    ),
                    center = Offset.Zero,
                    radius = 1.1f * r
                ),
                radius = 1.1f * r,
                center = Offset.Zero
            )

            // Glossy highlight on the upper-left.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = Offset(-0.4f * r, -0.42f * r),
                    radius = 0.36f * r
                ),
                radius = 0.36f * r,
                center = Offset(-0.4f * r, -0.42f * r)
            )
        }

        // Top dimple where the stem sits.
        drawOval(
            color = Color(0xFF8E0000).copy(alpha = 0.45f),
            topLeft = Offset(-0.18f * r, -0.74f * r),
            size = Size(0.36f * r, 0.2f * r)
        )

        // Bottom recess (black part at the base) to match the reference image.
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0A0402), // Almost black
                    Color(0xFF2D1A11).copy(alpha = 0.9f), // Dark brown
                    Color(0xFF5D4037).copy(alpha = 0.6f), // Fading brown
                    Color.Transparent
                ),
                center = Offset(0f, 0.72f * r),
                radius = 0.1f * r
            ),
            topLeft = Offset(-0.2f * r, 0.62f * r),
            size = Size(0.4f * r, 0.2f * r)
        )

        // Curved stem (reduced size).
        val stem = Path().apply {
            moveTo(0f, -0.68f * r)

            quadraticBezierTo(
                0.02f * r, -0.85f * r, // Adjusted control point
                0.10f * r, -1.05f * r  // Reduced endpoint height
            )
        }

        drawPath(
            path = stem,
            color = Color(0xFF4E342E),
            style = Stroke(
                width = 0.055f * r, // Reduced stroke width
                cap = StrokeCap.Round
            )
        )

        // Green leaf (adjusted position to match shorter stem).
        val leaf = Path().apply {
            moveTo(0.06f * r, -0.95f * r) // Lowered start point

            quadraticBezierTo(
                0.40f * r, -1.35f * r, // Adjusted control point
                0.85f * r, -1.15f * r  // Lowered end point
            )

            quadraticBezierTo(
                0.45f * r, -0.85f * r, // Adjusted control point
                0.06f * r, -0.95f * r
            )

            close()
        }

        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF81C784),
                    Color(0xFF2E7D32)
                ),
                start = Offset(0.06f * r, -0.95f * r),
                end = Offset(0.85f * r, -1.15f * r)
            )
        )

        // Leaf midrib (adjusted position).
        drawLine(
            color = Color(0xFFC8E6C9).copy(alpha = 0.6f),
            start = Offset(0.10f * r, -0.96f * r),
            end = Offset(0.75f * r, -1.12f * r),
            strokeWidth = 0.02f * r,
            cap = StrokeCap.Round
        )

        // Sharp specular highlight.
        rotate(
            degrees = -25f,
            pivot = Offset(-0.42f * r, -0.5f * r)
        ) {
            drawOval(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset(-0.48f * r, -0.56f * r),
                size = Size(0.14f * r, 0.26f * r)
            )
        }
    }
}