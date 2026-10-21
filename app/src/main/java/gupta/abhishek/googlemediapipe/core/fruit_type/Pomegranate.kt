package gupta.abhishek.googlemediapipe.core.fruit_type

import gupta.abhishek.googlemediapipe.core.fruits.FruitType

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
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
        seedCount = 100,
        sizeScale = 1.1f,
        draw = { radius -> drawPomegranate(radius) }
    )

    private fun DrawScope.drawPomegranate(r: Float) {

        // Nearly circular body with subtle natural lobes.
        val body = Path().apply {
            moveTo(0f, -0.88f * r)
            cubicTo(0.20f * r, -0.91f * r, 0.48f * r, -0.82f * r, 0.68f * r, -0.62f * r)
            cubicTo(0.89f * r, -0.40f * r, 0.94f * r, -0.12f * r, 0.91f * r, 0.17f * r)
            cubicTo(0.88f * r, 0.50f * r, 0.63f * r, 0.81f * r, 0.30f * r, 0.88f * r)
            cubicTo(0.18f * r, 0.92f * r, 0.08f * r, 0.89f * r, 0f, 0.86f * r)
            cubicTo(-0.08f * r, 0.89f * r, -0.18f * r, 0.92f * r, -0.30f * r, 0.88f * r)
            cubicTo(-0.63f * r, 0.81f * r, -0.88f * r, 0.50f * r, -0.91f * r, 0.17f * r)
            cubicTo(-0.94f * r, -0.12f * r, -0.89f * r, -0.40f * r, -0.68f * r, -0.62f * r)
            cubicTo(-0.48f * r, -0.82f * r, -0.20f * r, -0.91f * r, 0f, -0.88f * r)
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
            for (i in 0 until 120) {
                val angle = i * 2.39996f
                val distance = r * 0.87f * sqrt((i + 0.5f) / 120f)
                val x = cos(angle) * distance
                val y = sin(angle) * distance

                drawCircle(
                    color = Color(0xFF67051B).copy(alpha = 0.15f),
                    radius = r * 0.012f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color(0xFFFFB0BA).copy(alpha = 0.25f),
                    radius = r * 0.006f,
                    center = Offset(x - r * 0.012f, y - r * 0.012f)
                )
            }

            // Subtle yellowish/orange tint near the crown (common in real pomegranates)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFF4C27A).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(0f, -0.8f * r),
                    radius = 0.5f * r
                ),
                radius = 0.5f * r,
                center = Offset(0f, -0.8f * r)
            )

            // Soft shadow on the lower-right area.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0xFF4B0015).copy(alpha = 0.45f)),
                    center = Offset(0.42f * r, 0.43f * r),
                    radius = 1.15f * r
                ),
                radius = r,
                center = Offset.Zero
            )

            // Broad glossy reflection on the upper-left.
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.05f), Color.Transparent),
                    start = Offset(-0.75f * r, -0.72f * r),
                    end = Offset(0.0f, 0.05f * r)
                ),
                topLeft = Offset(-0.76f * r, -0.73f * r),
                size = Size(0.43f * r, 0.85f * r)
            )

            // Small bright reflection.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(-0.43f * r, -0.43f * r),
                    radius = 0.29f * r
                ),
                radius = 0.29f * r,
                center = Offset(-0.43f * r, -0.43f * r)
            )
        }

        // --- SMALLER, TIGHTER CROWN (CALYX) ---
        // Shadow beneath the crown (kept close to the fruit surface).
        drawOval(
            color = Color(0xFF2D0009).copy(alpha = 0.6f),
            topLeft = Offset(-0.28f * r, -0.90f * r),
            size = Size(0.56f * r, 0.18f * r)
        )

        // 1. Back layer of jagged sepals — shorter, less splayed.
        val backCrown = Path().apply {
            moveTo(-0.28f * r, -0.88f * r)
            lineTo(-0.36f * r, -0.99f * r) // Left spike
            lineTo(-0.17f * r, -0.95f * r)
            lineTo(0.02f * r, -1.02f * r)  // Top spike
            lineTo(0.11f * r, -0.92f * r)
            lineTo(0.30f * r, -0.98f * r)  // Right spike
            lineTo(0.28f * r, -0.86f * r)
            close()
        }
        drawPath(
            path = backCrown,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF8C2B17), Color(0xFF4A1105)),
                start = Offset(0f, -1.05f * r),
                end = Offset(0f, -0.8f * r)
            )
        )

        // 2. Main fleshy cup of the crown — shorter and narrower.
        val cup = Path().apply {
            moveTo(-0.26f * r, -0.84f * r)
            cubicTo(-0.32f * r, -0.91f * r, -0.30f * r, -0.98f * r, -0.21f * r, -0.96f * r)
            cubicTo(-0.12f * r, -0.93f * r, -0.04f * r, -1.00f * r, 0.04f * r, -0.96f * r)
            cubicTo(0.13f * r, -0.93f * r, 0.24f * r, -0.99f * r, 0.26f * r, -0.88f * r)
            cubicTo(0.25f * r, -0.77f * r, 0.13f * r, -0.73f * r, 0f, -0.73f * r)
            cubicTo(-0.13f * r, -0.73f * r, -0.22f * r, -0.77f * r, -0.26f * r, -0.84f * r)
            close()
        }
        drawPath(
            path = cup,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE88D67), // Yellowish-red tip
                    Color(0xFFB53A1F), // Rust red
                    Color(0xFF5C1508)  // Dark brown base
                ),
                center = Offset(0f, -0.78f * r),
                radius = 0.28f * r
            )
        )

        // 3. Front prominent spikes (sepals) — shorter and closer to the cup.
        fun DrawScope.drawSepal(startX: Float, startY: Float, endX: Float, endY: Float, ctrlX: Float, ctrlY: Float) {
            val path = Path().apply {
                moveTo(startX, startY)
                quadraticBezierTo(ctrlX, ctrlY, endX, endY)
                lineTo(startX + (startX - endX) * 0.3f, startY + (startY - endY) * 0.1f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFD4A353), Color(0xFF9E3017), Color(0xFF3B0A02)),
                    start = Offset(endX, endY),
                    end = Offset(startX, startY)
                )
            )
        }

        // Left spike
        drawSepal(-0.21f * r, -0.86f * r, -0.35f * r, -1.00f * r, -0.31f * r, -0.93f * r)
        // Center-left spike
        drawSepal(-0.08f * r, -0.84f * r, -0.13f * r, -1.04f * r, -0.16f * r, -0.94f * r)
        // Center-right spike
        drawSepal(0.05f * r, -0.84f * r, 0.09f * r, -1.02f * r, 0.13f * r, -0.93f * r)
        // Right spike
        drawSepal(0.21f * r, -0.86f * r, 0.33f * r, -0.99f * r, 0.29f * r, -0.91f * r)

        // 4. Deep central cavity (the hole where the flower used to be) — smaller.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1A0802), Color(0xFF3B0A02), Color(0xFF6A1A0A)),
                center = Offset(0f, -0.88f * r),
                radius = 0.12f * r
            ),
            radius = 0.12f * r,
            center = Offset(0f, -0.88f * r)
        )

        // 5. Dried stamens and debris inside the cavity — tighter cluster.
        rotate(degrees = 15f, pivot = Offset(0f, -0.88f * r)) {
            for (i in 0 until 18) {
                val angle = (i / 18f) * (2 * Math.PI).toFloat()
                val dist = 0.025f * r + (i % 3) * 0.012f * r
                val x = cos(angle) * dist
                val y = sin(angle) * dist - 0.88f * r
                drawLine(
                    color = if (i % 2 == 0) Color(0xFFC89B55) else Color(0xFF8C2B17),
                    start = Offset(x, y),
                    end = Offset(x * 1.1f, y - 0.015f * r),
                    strokeWidth = 0.006f * r,
                    cap = StrokeCap.Round
                )
            }
        }

        // Tiny dark seeds/debris in the center — smaller cluster.
        drawCircle(color = Color(0xFF2A0B04), radius = 0.015f * r, center = Offset(0f, -0.88f * r))
        drawCircle(color = Color(0xFF5A1B0C), radius = 0.010f * r, center = Offset(0.02f * r, -0.89f * r))
        drawCircle(color = Color(0xFF5A1B0C), radius = 0.010f * r, center = Offset(-0.02f * r, -0.87f * r))

        // --- REALISTIC BOTTOM (BLOSSOM SCAR) — unchanged ---
        // A natural pomegranate has a small, star-shaped dried scar at the base.

        // Slight shadow to show the indentation
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4A0015).copy(alpha = 0.7f), Color.Transparent),
                center = Offset(0f, 0.86f * r),
                radius = 0.15f * r
            ),
            radius = 0.15f * r,
            center = Offset(0f, 0.86f * r)
        )

        // The star-shaped scar
        val bottomScar = Path().apply {
            val centerX = 0f
            val centerY = 0.86f * r
            val points = 5
            val outerRadius = 0.08f * r
            val innerRadius = 0.04f * r

            for (i in 0 until points * 2) {
                val angle = (i * Math.PI / points).toFloat() - (Math.PI / 2).toFloat()
                val rPoint = if (i % 2 == 0) outerRadius else innerRadius
                val x = centerX + cos(angle) * rPoint
                val y = centerY + sin(angle) * rPoint

                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        drawPath(
            path = bottomScar,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF5C1508), // Dark brown center
                    Color(0xFF8C2B17), // Rusty edges
                    Color(0xFFB53A1F)
                ),
                center = Offset(0f, 0.86f * r),
                radius = 0.08f * r
            )
        )

        // Tiny wrinkles/radiating lines around the bottom scar
        for (i in 0 until 8) {
            val angle = (i / 8f) * (2 * Math.PI).toFloat()
            val startX = cos(angle) * 0.07f * r
            val startY = sin(angle) * 0.07f * r + 0.86f * r
            val endX = cos(angle) * 0.12f * r
            val endY = sin(angle) * 0.12f * r + 0.86f * r

            drawLine(
                color = Color(0xFF67051B).copy(alpha = 0.4f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 0.01f * r,
                cap = StrokeCap.Round
            )
        }
    }
}