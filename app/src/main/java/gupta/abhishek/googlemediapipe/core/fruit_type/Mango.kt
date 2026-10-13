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
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object Mango {

    val type = FruitType(
        name = "mango",
        skinColor = Color(0xFFFBC02D),
        juiceColor = Color(0xFFFFB300),
        insideColor = Color(0xFFFFCA28),
        seedColor = Color(0xFFD7A86E),
        seedCount = 50, sizeScale = 1f,
        draw = { radius -> drawMango(radius) }
    )

    // Pre-computed noise seed for consistent texture across frames
    private val noiseSeed = Random(42)
    private val lenticels = List(60) {
        val angle = noiseSeed.nextFloat() * 2f * Math.PI.toFloat()
        val dist = 0.3f + noiseSeed.nextFloat() * 0.7f
        Lenticel(
            angle = angle,
            distance = dist,
            radius = 0.008f + noiseSeed.nextFloat() * 0.015f,
            alpha = 0.15f + noiseSeed.nextFloat() * 0.25f
        )
    }

    // Water droplet positions for realistic condensation
    private val droplets = List(12) {
        WaterDroplet(
            x = -0.7f + noiseSeed.nextFloat() * 1.4f,
            y = -0.8f + noiseSeed.nextFloat() * 1.2f,
            radius = 0.02f + noiseSeed.nextFloat() * 0.04f,
            opacity = 0.4f + noiseSeed.nextFloat() * 0.4f
        )
    }

    private data class Lenticel(
        val angle: Float,
        val distance: Float,
        val radius: Float,
        val alpha: Float
    )

    private data class WaterDroplet(
        val x: Float,
        val y: Float,
        val radius: Float,
        val opacity: Float
    )

    private fun DrawScope.drawMango(r: Float) {
        // Mango is an oval, taller than wide, tilted a little.
        val rx = 0.82f * r
        val ry = 1.0f * r

        // More organic mango shape using bezier curves instead of perfect oval
        val body = Path().apply {
            // Top point
            moveTo(0f, -ry)
            // Right side - upper curve (slightly flattened)
            cubicTo(
                0.55f * rx, -ry,
                rx, -0.6f * ry,
                rx, 0f
            )
            // Right side - lower curve (fuller)
            cubicTo(
                rx, 0.55f * ry,
                0.6f * rx, ry,
                0f, ry
            )
            // Left side - lower curve
            cubicTo(
                -0.6f * rx, ry,
                -rx, 0.55f * ry,
                -rx, 0f
            )
            // Left side - upper curve (slightly flattened)
            cubicTo(
                -rx, -0.6f * ry,
                -0.55f * rx, -ry,
                0f, -ry
            )
            close()
        }

        rotate(degrees = -12f, pivot = Offset.Zero) {

            // === BASE SKIN with realistic gradient ===
            drawPath(
                path = body,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4), // Bright yellow highlight
                        Color(0xFFFFEE58), // Yellow
                        Color(0xFFFDD835), // Golden
                        Color(0xFFF9A825), // Deep golden
                        Color(0xFFEF6C00), // Orange edge
                        Color(0xFFBF360C)  // Dark red-orange rim
                    ),
                    center = Offset(-0.3f * rx, -0.35f * ry),
                    radius = 1.7f * r
                )
            )

            clipPath(body) {
                // === RED BLUSH (sun-exposed side) with smooth gradient ===
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD32F2F).copy(alpha = 0.7f),
                            Color(0xFFE53935).copy(alpha = 0.5f),
                            Color(0xFFFF7043).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(0.6f * rx, -0.1f * ry),
                        radius = 1.1f * r
                    ),
                    topLeft = Offset(-rx * 1.2f, -ry * 1.1f),
                    size = Size(2.4f * rx, 2.2f * ry)
                )

                // Secondary blush on lower right
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE65100).copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f * rx, 0.5f * ry),
                        radius = 0.7f * r
                    ),
                    topLeft = Offset(-rx, -ry),
                    size = Size(2f * rx, 2f * ry)
                )

                // === LENTICELS (natural skin spots) ===
                lenticels.forEach { lenticel ->
                    val cx = cos(lenticel.angle) * lenticel.distance * rx * 0.85f
                    val cy = sin(lenticel.angle) * lenticel.distance * ry * 0.9f

                    // Skip spots too close to edge
                    if (lenticel.distance < 0.95f) {
                        // Dark dot
                        drawCircle(
                            color = Color(0xFF5D4037).copy(alpha = lenticel.alpha * 0.6f),
                            radius = r * lenticel.radius,
                            center = Offset(cx, cy)
                        )
                        // Highlight around dot for dimension
                        drawCircle(
                            color = Color.White.copy(alpha = 0.08f),
                            radius = r * lenticel.radius * 1.8f,
                            center = Offset(cx - r * 0.005f, cy - r * 0.005f)
                        )
                    }
                }

                // === DEEP SHADOW at bottom-right for 3D roundness ===
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF3E2723).copy(alpha = 0.15f),
                            Color(0xFF3E2723).copy(alpha = 0.4f)
                        ),
                        center = Offset(0.3f * rx, 0.2f * ry),
                        radius = 1.4f * r
                    ),
                    topLeft = Offset(-rx, -ry),
                    size = Size(2f * rx, 2f * ry)
                )

                // === WATER SIMULATION: Condensation droplets ===
                droplets.forEach { droplet ->
                    val dx = droplet.x * rx
                    val dy = droplet.y * ry

                    // Skip droplets outside the body shape
                    val distFromCenter = sqrt(
                        (dx / rx).pow(2) + (dy / ry).pow(2)
                    )
                    if (distFromCenter > 0.95f) return@forEach

                    val dropR = r * droplet.radius

                    // Droplet shadow beneath
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f * droplet.opacity),
                        radius = dropR * 1.1f,
                        center = Offset(dx + dropR * 0.3f, dy + dropR * 0.4f)
                    )

                    // Main droplet body (refractive look)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f * droplet.opacity),
                                Color(0xFFE3F2FD).copy(alpha = 0.3f * droplet.opacity),
                                Color(0xFF90CAF9).copy(alpha = 0.15f * droplet.opacity),
                                Color.Transparent
                            ),
                            center = Offset(dx - dropR * 0.3f, dy - dropR * 0.3f),
                            radius = dropR * 1.5f
                        ),
                        radius = dropR,
                        center = Offset(dx, dy)
                    )

                    // Bright specular highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f * droplet.opacity),
                        radius = dropR * 0.3f,
                        center = Offset(dx - dropR * 0.25f, dy - dropR * 0.25f)
                    )

                    // Secondary tiny highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f * droplet.opacity),
                        radius = dropR * 0.12f,
                        center = Offset(dx + dropR * 0.3f, dy + dropR * 0.2f)
                    )

                    // Caustic reflection (light passing through)
                    drawCircle(
                        color = Color(0xFFFFF9C4).copy(alpha = 0.3f * droplet.opacity),
                        radius = dropR * 0.5f,
                        center = Offset(dx + dropR * 0.2f, dy + dropR * 0.35f)
                    )
                }

                // === WET LOOK: Water film sheen over whole surface ===
                drawPath(
                    path = body,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color(0xFF90CAF9).copy(alpha = 0.04f),
                            Color.Transparent,
                            Color(0xFF1A237E).copy(alpha = 0.05f)
                        ),
                        center = Offset(-0.2f * rx, -0.3f * ry),
                        radius = 1.5f * r
                    )
                )

                // === GLOSSY HIGHLIGHT on upper left (main light reflection) ===
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(-0.4f * rx, -0.45f * ry),
                        radius = 0.45f * r
                    ),
                    topLeft = Offset(-0.8f * rx, -0.85f * ry),
                    size = Size(0.8f * r, 0.75f * r)
                )

                // Secondary specular highlight (smaller, brighter)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.Transparent
                        ),
                        center = Offset(-0.42f * rx, -0.48f * ry),
                        radius = 0.2f * r
                    ),
                    topLeft = Offset(-0.62f * rx, -0.68f * ry),
                    size = Size(0.4f * r, 0.4f * r)
                )

                // === REFLECTED LIGHT on bottom edge (bounce light) ===
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFECB3).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(0.1f * rx, 0.85f * ry),
                        radius = 0.5f * r
                    ),
                    topLeft = Offset(-0.4f * rx, 0.6f * ry),
                    size = Size(0.8f * r, 0.5f * r)
                )

                // === WATER RUNNEL (a streak of water flowing down) ===
                val runnelPath = Path().apply {
                    moveTo(0.15f * rx, -0.7f * ry)
                    cubicTo(
                        0.2f * rx, -0.3f * ry,
                        0.1f * rx, 0.1f * ry,
                        0.18f * rx, 0.5f * ry
                    )
                }
                drawPath(
                    path = runnelPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        start = Offset(0.15f * rx, -0.7f * ry),
                        end = Offset(0.18f * rx, 0.5f * ry)
                    ),
                    style = Stroke(width = r * 0.06f, cap = StrokeCap.Round)
                )

                // Runnel highlight edge
                drawPath(
                    path = runnelPath,
                    color = Color.White.copy(alpha = 0.15f),
                    style = Stroke(width = r * 0.02f, cap = StrokeCap.Round)
                )
            }

            // === STEM ===
            val stem = Path().apply {
                moveTo(0f, -0.95f * ry)
                quadraticBezierTo(0.03f * r, -1.08f * ry, 0.1f * r, -1.18f * ry)
            }
            // Stem shadow
            drawPath(
                path = stem,
                color = Color(0xFF3E2723),
                style = Stroke(width = 0.09f * r, cap = StrokeCap.Round)
            )
            // Stem main color
            drawPath(
                path = stem,
                color = Color(0xFF5D4037),
                style = Stroke(width = 0.07f * r, cap = StrokeCap.Round)
            )
            // Stem highlight
            val stemHighlight = Path().apply {
                moveTo(-0.01f * r, -0.97f * ry)
                quadraticBezierTo(0.02f * r, -1.08f * ry, 0.09f * r, -1.17f * ry)
            }
            drawPath(
                path = stemHighlight,
                color = Color(0xFF8D6E63),
                style = Stroke(width = 0.02f * r, cap = StrokeCap.Round)
            )

            // === LEAF with realistic details ===
            val leaf = Path().apply {
                moveTo(0.04f * r, -1.0f * ry)
                // Upper edge with slight curve
                cubicTo(
                    0.15f * r, -1.15f * ry,
                    0.4f * r, -1.25f * ry,
                    0.6f * r, -1.12f * ry
                )
                // Lower edge returning
                cubicTo(
                    0.45f * r, -1.0f * ry,
                    0.2f * r, -0.95f * ry,
                    0.04f * r, -1.0f * ry
                )
                close()
            }

            // Leaf shadow
            drawPath(
                path = leaf,
                color = Color(0xFF1B5E20).copy(alpha = 0.4f)
            )

            // Leaf main with gradient
            drawPath(
                path = leaf,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF81C784),
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32),
                        Color(0xFF1B5E20)
                    ),
                    start = Offset(0.04f * r, -1.0f * ry),
                    end = Offset(0.6f * r, -1.12f * ry)
                )
            )

            // Leaf vein (midrib)
            val vein = Path().apply {
                moveTo(0.06f * r, -1.0f * ry)
                quadraticBezierTo(0.3f * r, -1.12f * ry, 0.57f * r, -1.11f * ry)
            }
            drawPath(
                path = vein,
                color = Color(0xFF1B5E20).copy(alpha = 0.6f),
                style = Stroke(width = 0.015f * r, cap = StrokeCap.Round)
            )

            // Leaf highlight
            drawPath(
                path = leaf,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(0.1f * r, -1.15f * ry),
                    end = Offset(0.35f * r, -1.05f * ry)
                )
            )

            // === WATER DROPLET ON LEAF ===
            val leafDropX = 0.35f * r
            val leafDropY = -1.1f * ry
            val leafDropR = 0.035f * r

            // Drop shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.2f),
                radius = leafDropR * 1.1f,
                center = Offset(leafDropX + leafDropR * 0.3f, leafDropY + leafDropR * 0.4f)
            )
            // Drop body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color(0xFFE3F2FD).copy(alpha = 0.5f),
                        Color(0xFF90CAF9).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(leafDropX - leafDropR * 0.3f, leafDropY - leafDropR * 0.3f),
                    radius = leafDropR * 1.5f
                ),
                radius = leafDropR,
                center = Offset(leafDropX, leafDropY)
            )
            // Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = leafDropR * 0.3f,
                center = Offset(leafDropX - leafDropR * 0.25f, leafDropY - leafDropR * 0.25f)
            )
        }
    }
}