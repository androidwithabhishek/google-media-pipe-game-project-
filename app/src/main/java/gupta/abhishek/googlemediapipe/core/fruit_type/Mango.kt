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
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.core.fruits.FruitType

object Mango {

    val type = FruitType(
        name = "mango",
        skinColor = Color(0xFFFBC02D),
        juiceColor = Color(0xFFFFB300),
        insideColor = Color(0xFFFFCA28),
        seedColor = Color(0xFFD7A86E),
        seedCount = 50,
        sizeScale = 1f,
        draw = { radius -> drawMango(radius) }
    )

    private fun DrawScope.drawMango(r: Float) {
        val rx = 0.8f * r
        val ry = 1.0f * r

        // Smooth teardrop/kidney shape matching the reference image
        val body = Path().apply {
            moveTo(-0.1f * rx, -0.85f * ry)
            // Top right curve (shoulder)
            cubicTo(
                0.4f * rx, -0.85f * ry,
                0.9f * rx, -0.35f * ry,
                0.8f * rx, 0.25f * ry
            )
            // Bottom right curve (fuller)
            cubicTo(
                0.7f * rx, 0.8f * ry,
                0.2f * rx, 1.0f * ry,
                -0.3f * rx, 0.85f * ry
            )
            // Bottom left curve (kidney indent)
            cubicTo(
                -0.7f * rx, 0.7f * ry,
                -0.9f * rx, 0.0f,
                -0.6f * rx, -0.5f * ry
            )
            // Left shoulder back to top
            cubicTo(
                -0.4f * rx, -0.8f * ry,
                -0.2f * rx, -0.85f * ry,
                -0.1f * rx, -0.85f * ry
            )
            close()
        }

        // 1. BASE SKIN with realistic 3D volume gradient
        drawPath(
            path = body,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF9C4), // Bright intense highlight (top-left)
                    Color(0xFFFFEE58), // Bright yellow
                    Color(0xFFFBC02D), // Base golden yellow
                    Color(0xFFF57F17), // Deep orange edge
                    Color(0xFFE65100)  // Darkest rim shadow
                ),
                center = Offset(-0.35f * rx, -0.35f * ry),
                radius = 1.5f * r
            )
        )

        clipPath(body) {
            // 2. Ambient Occlusion / Core Shadow on the bottom right
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFBF360C).copy(alpha = 0.45f)
                    ),
                    center = Offset(-0.1f * rx, -0.1f * ry),
                    radius = 1.3f * r
                ),
                topLeft = Offset(-rx, -ry),
                size = Size(2f * rx, 2f * ry)
            )

            // 3. Broad Diffuse Highlight (Soft Light Reflection)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(-0.4f * rx, -0.4f * ry),
                    radius = 0.65f * r
                ),
                radius = 0.65f * r,
                center = Offset(-0.4f * rx, -0.4f * ry)
            )

            // 4. Rim Light / Bounce Light on the bottom-left edge
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE082).copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(-0.6f * rx, 0.6f * ry),
                    radius = 0.45f * r
                ),
                topLeft = Offset(-0.9f * rx, 0.2f * ry),
                size = Size(0.7f * r, 0.7f * r)
            )

            // 5. Crisp Specular Highlight (The "Shine")
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color.White.copy(alpha = 0.0f)
                    ),
                    start = Offset(-0.45f * rx, -0.55f * ry),
                    end = Offset(-0.2f * rx, -0.2f * ry)
                ),
                topLeft = Offset(-0.55f * rx, -0.65f * ry),
                size = Size(0.35f * r, 0.5f * r)
            )

            // Secondary sharp highlight dot (adds glossy feel)
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 0.04f * r,
                center = Offset(-0.35f * rx, -0.45f * ry)
            )
        }

        // === STEM SHADOW ON BODY ===
        drawOval(
            color = Color(0xFF3E2723).copy(alpha = 0.4f),
            topLeft = Offset(-0.18f * rx, -0.88f * ry),
            size = Size(0.2f * r, 0.1f * r)
        )

        // === STEM ===
        val stem = Path().apply {
            moveTo(-0.1f * rx, -0.84f * ry)
            quadraticBezierTo(
                -0.02f * rx, -1.0f * ry,
                0.06f * rx, -1.1f * ry
            )
        }

        // Stem base dark shadow
        drawPath(
            path = stem,
            color = Color(0xFF2D1A14),
            style = Stroke(width = 0.07f * r, cap = StrokeCap.Round)
        )
        // Stem mid-tone
        drawPath(
            path = stem,
            color = Color(0xFF5D4037),
            style = Stroke(width = 0.05f * r, cap = StrokeCap.Round)
        )
        // Stem specular highlight
        val stemHighlight = Path().apply {
            moveTo(-0.11f * rx, -0.85f * ry)
            quadraticBezierTo(
                -0.03f * rx, -1.0f * ry,
                0.05f * rx, -1.09f * ry
            )
        }
        drawPath(
            path = stemHighlight,
            color = Color(0xFFBCAAA4),
            style = Stroke(width = 0.015f * r, cap = StrokeCap.Round)
        )

        // === LEAF ===
        val leaf = Path().apply {
            moveTo(0.02f * rx, -1.05f * ry)
            // Upper curve to tip
            cubicTo(
                0.25f * r, -1.25f * ry,
                0.6f * r, -1.2f * ry,
                0.85f * r, -1.0f * ry
            )
            // Pointed tip
            cubicTo(
                0.9f * r, -0.95f * ry,
                0.92f * r, -0.92f * ry,
                0.88f * r, -0.9f * ry
            )
            // Lower curve back to base
            cubicTo(
                0.6f * r, -0.8f * ry,
                0.25f * r, -0.8f * ry,
                0.02f * rx, -1.05f * ry
            )
            close()
        }

        // Leaf cast shadow on the mango body (offsets down and left)
        rotate(degrees = -5f, pivot = Offset(0.4f * r, -1.0f * ry)) {
            drawPath(
                path = leaf,
                color = Color(0xFF1B5E20).copy(alpha = 0.35f),
                style = Stroke(width = 0.08f * r, cap = StrokeCap.Round)
            )
        }

        // Leaf main 3D gradient
        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFC5E1A5), // Bright top edge
                    Color(0xFF7CB342), // Mid green
                    Color(0xFF388E3C), // Deep green
                    Color(0xFF1B5E20)  // Shadow side
                ),
                start = Offset(0.1f * r, -1.2f * ry),
                end = Offset(0.7f * r, -0.9f * ry)
            )
        )

        // Leaf rim light (top edge catching the light)
        val leafRim = Path().apply {
            moveTo(0.05f * r, -1.05f * ry)
            cubicTo(
                0.25f * r, -1.23f * ry,
                0.6f * r, -1.18f * ry,
                0.82f * r, -1.0f * ry
            )
        }
        drawPath(
            path = leafRim,
            color = Color(0xFFDCEDC8).copy(alpha = 0.7f),
            style = Stroke(width = 0.015f * r, cap = StrokeCap.Round)
        )

        // Leaf midrib (Central Vein)
        val midrib = Path().apply {
            moveTo(0.05f * r, -1.04f * ry)
            quadraticBezierTo(
                0.4f * r, -1.05f * ry,
                0.85f * r, -0.95f * ry
            )
        }
        drawPath(
            path = midrib,
            color = Color(0xFF2E7D32).copy(alpha = 0.8f),
            style = Stroke(width = 0.02f * r, cap = StrokeCap.Round)
        )

        // Midrib highlight
        drawPath(
            path = midrib,
            color = Color(0xFFA5D6A7).copy(alpha = 0.5f),
            style = Stroke(width = 0.008f * r, cap = StrokeCap.Round)
        )

        // Side veins (branching off the midrib)
        val sideVeins = listOf(
            // Top veins
            Offset(0.25f * r, -1.08f * ry) to Offset(0.35f * r, -1.18f * ry),
            Offset(0.45f * r, -1.05f * ry) to Offset(0.6f * r, -1.12f * ry),
            Offset(0.65f * r, -1.01f * ry) to Offset(0.75f * r, -1.05f * ry),
            // Bottom veins
            Offset(0.25f * r, -1.01f * ry) to Offset(0.35f * r, -0.9f * ry),
            Offset(0.45f * r, -0.98f * ry) to Offset(0.6f * r, -0.87f * ry),
            Offset(0.65f * r, -0.96f * ry) to Offset(0.75f * r, -0.91f * ry)
        )

        sideVeins.forEach { (start, end) ->
            drawLine(
                color = Color(0xFF2E7D32).copy(alpha = 0.5f),
                start = start,
                end = end,
                strokeWidth = 0.01f * r,
                cap = StrokeCap.Round
            )
        }

        // Leaf surface specular shine (glossy top layer)
        drawPath(
            path = leaf,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                center = Offset(0.3f * r, -1.1f * ry),
                radius = 0.25f * r
            )
        )
    }
}