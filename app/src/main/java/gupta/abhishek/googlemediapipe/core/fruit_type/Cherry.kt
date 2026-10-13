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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object Cherry {

    val type = FruitType(
        name = "cherry",
        skinColor = Color(0xFFB71C1C),
        juiceColor = Color(0xFFD32F2F),
        insideColor = Color(0xFFE57373),
        seedColor = Color(0xFF8D6E63),
        seedCount = 3,
        sizeScale = 0.9f,
        draw = { radius -> drawCherries(radius) }
    )

    private val rng = Random(1337)

    private val lenticels = List(45) {
        Lenticel(
            angle = rng.nextFloat() * 2f * PI.toFloat(),
            distance = 0.25f + rng.nextFloat() * 0.7f,
            radius = 0.006f + rng.nextFloat() * 0.012f,
            alpha = 0.12f + rng.nextFloat() * 0.2f
        )
    }

    private val droplets = List(9) {
        WaterDroplet(
            x = -0.65f + rng.nextFloat() * 1.3f,
            y = -0.65f + rng.nextFloat() * 1.3f,
            radius = 0.025f + rng.nextFloat() * 0.035f,
            opacity = 0.5f + rng.nextFloat() * 0.4f
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

    private data class CherryLayout(
        val cx: Float,
        val cy: Float,
        val scale: Float,
        val tilt: Float
    )

    // ============================================================
    // ONLY CHANGE: outer cherries pulled inward (cx was ±0.75)
    // Everything else (cy, scale, tilt) is identical to original.
    // ============================================================
    private val layout = listOf(
        CherryLayout(cx = -0.48f, cy = 0.15f, scale = 0.85f, tilt = -18f),
        CherryLayout(cx = 0.48f, cy = 0.10f, scale = 0.90f, tilt = 22f),
        CherryLayout(cx = 0.00f, cy = 0.35f, scale = 1.00f, tilt = 0f)
    )

    private fun DrawScope.drawCherries(r: Float) {
        layout.sortedBy { it.cy }.forEachIndexed { index, cherry ->
            val isFront = cherry.cy == layout.maxOf { it.cy }
            drawSingleCherry(
                centerX = cherry.cx * r,
                centerY = cherry.cy * r,
                radius = r * 0.55f * cherry.scale,
                tilt = cherry.tilt,
                isFront = isFront
            )
        }
        drawStems(r)
    }

    private fun DrawScope.drawSingleCherry(
        centerX: Float,
        centerY: Float,
        radius: Float,
        tilt: Float,
        isFront: Boolean
    ) {
        val r = radius
        val rx = r
        val ry = r * 1.05f

        val body = Path().apply {
            moveTo(0f, -0.92f * ry)
            cubicTo(
                0.5f * rx, -1.02f * ry,
                1.02f * rx, -0.5f * ry,
                0.98f * rx, 0.05f * ry
            )
            cubicTo(
                0.92f * rx, 0.6f * ry,
                0.45f * rx, 1.0f * ry,
                0f, 0.95f * ry
            )
            cubicTo(
                -0.45f * rx, 1.0f * ry,
                -0.92f * rx, 0.6f * ry,
                -0.98f * rx, 0.05f * ry
            )
            cubicTo(
                -1.02f * rx, -0.5f * ry,
                -0.5f * rx, -1.02f * ry,
                0f, -0.92f * ry
            )
            close()
        }

        rotate(degrees = tilt, pivot = Offset(centerX, centerY)) {
            val cX = centerX
            val cY = centerY

            val translatedBody = Path().apply {
                addPath(body)
                translate(Offset(cX, cY))
            }

            // Base gradient
            drawPath(
                path = translatedBody,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFE53935),
                        Color(0xFFC62828),
                        Color(0xFF8E0000),
                        Color(0xFF4A0000)
                    ),
                    center = Offset(cX - 0.35f * rx, cY - 0.4f * ry),
                    radius = 1.7f * r
                )
            )

            clipPath(translatedBody) {
                // Darker bottom
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF2D0000).copy(alpha = 0.5f)
                        ),
                        center = Offset(cX + 0.15f * rx, cY + 0.25f * ry),
                        radius = 1.25f * r
                    ),
                    topLeft = Offset(cX - rx, cY - ry),
                    size = Size(2f * rx, 2f * ry)
                )

                // Lenticels
                lenticels.forEach { lenticel ->
                    val lx = cX + cos(lenticel.angle) * lenticel.distance * rx * 0.85f
                    val ly = cY + sin(lenticel.angle) * lenticel.distance * ry * 0.9f

                    if (lenticel.distance < 0.95f) {
                        drawCircle(
                            color = Color(0xFF3E0000).copy(alpha = lenticel.alpha * 0.7f),
                            radius = r * lenticel.radius,
                            center = Offset(lx, ly)
                        )
                        drawCircle(
                            color = Color(0xFFFFCDD2).copy(alpha = lenticel.alpha * 0.4f),
                            radius = r * lenticel.radius * 0.5f,
                            center = Offset(lx - r * 0.004f, ly - r * 0.004f)
                        )
                    }
                }

                // Water film
                drawPath(
                    path = translatedBody,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color(0xFF90CAF9).copy(alpha = 0.05f),
                            Color.Transparent,
                            Color(0xFF0D47A1).copy(alpha = 0.06f)
                        ),
                        center = Offset(cX - 0.25f * rx, cY - 0.3f * ry),
                        radius = 1.6f * r
                    )
                )

                // Main glossy highlight
                val highlightPath = Path().apply {
                    moveTo(cX - 0.55f * rx, cY - 0.35f * ry)
                    cubicTo(
                        cX - 0.45f * rx, cY - 0.75f * ry,
                        cX - 0.05f * rx, cY - 0.82f * ry,
                        cX + 0.15f * rx, cY - 0.7f * ry
                    )
                    cubicTo(
                        cX - 0.05f * rx, cY - 0.68f * ry,
                        cX - 0.4f * rx, cY - 0.6f * ry,
                        cX - 0.55f * rx, cY - 0.35f * ry
                    )
                    close()
                }
                drawPath(
                    path = highlightPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        start = Offset(cX - 0.55f * rx, cY - 0.75f * ry),
                        end = Offset(cX + 0.15f * rx, cY - 0.35f * ry)
                    )
                )

                // Secondary highlight
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(cX - 0.5f * rx, cY - 0.55f * ry),
                        radius = 0.22f * r
                    ),
                    topLeft = Offset(cX - 0.72f * rx, cY - 0.77f * ry),
                    size = Size(0.44f * r, 0.44f * r)
                )

                // Reflected bottom light
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF8A80).copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(cX + 0.1f * rx, cY + 0.78f * ry),
                        radius = 0.45f * r
                    ),
                    topLeft = Offset(cX - 0.35f * rx, cY + 0.55f * ry),
                    size = Size(0.7f * r, 0.45f * r)
                )

                // Rim light
                val rimPath = Path().apply {
                    moveTo(cX + 0.85f * rx, cY - 0.35f * ry)
                    cubicTo(
                        cX + 0.98f * rx, cY - 0.1f * ry,
                        cX + 0.98f * rx, cY + 0.25f * ry,
                        cX + 0.85f * rx, cY + 0.55f * ry
                    )
                }
                drawPath(
                    path = rimPath,
                    color = Color(0xFFFF8A80).copy(alpha = 0.35f),
                    style = Stroke(width = r * 0.04f, cap = StrokeCap.Round)
                )

                // Droplets
                droplets.forEach { droplet ->
                    val dx = cX + droplet.x * rx
                    val dy = cY + droplet.y * ry

                    val distFromCenter = sqrt(
                        ((dx - cX) / rx).pow(2) + ((dy - cY) / ry).pow(2)
                    )
                    if (distFromCenter > 0.92f) return@forEach

                    val dropR = r * droplet.radius

                    drawCircle(
                        color = Color.Black.copy(alpha = 0.2f * droplet.opacity),
                        radius = dropR * 1.1f,
                        center = Offset(dx + dropR * 0.3f, dy + dropR * 0.4f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.75f * droplet.opacity),
                                Color(0xFFE3F2FD).copy(alpha = 0.35f * droplet.opacity),
                                Color(0xFF90CAF9).copy(alpha = 0.18f * droplet.opacity),
                                Color.Transparent
                            ),
                            center = Offset(dx - dropR * 0.3f, dy - dropR * 0.3f),
                            radius = dropR * 1.5f
                        ),
                        radius = dropR,
                        center = Offset(dx, dy)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.95f * droplet.opacity),
                        radius = dropR * 0.32f,
                        center = Offset(dx - dropR * 0.28f, dy - dropR * 0.28f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.55f * droplet.opacity),
                        radius = dropR * 0.12f,
                        center = Offset(dx + dropR * 0.3f, dy + dropR * 0.22f)
                    )
                    drawCircle(
                        color = Color(0xFFFFCDD2).copy(alpha = 0.35f * droplet.opacity),
                        radius = dropR * 0.45f,
                        center = Offset(dx + dropR * 0.15f, dy + dropR * 0.35f)
                    )
                }

                // Water runnel
                val runnelPath = Path().apply {
                    moveTo(cX - 0.15f * rx, cY - 0.7f * ry)
                    cubicTo(
                        cX - 0.05f * rx, cY - 0.2f * ry,
                        cX - 0.25f * rx, cY + 0.2f * ry,
                        cX - 0.15f * rx, cY + 0.6f * ry
                    )
                }
                drawPath(
                    path = runnelPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        start = Offset(cX - 0.15f * rx, cY - 0.7f * ry),
                        end = Offset(cX - 0.15f * rx, cY + 0.6f * ry)
                    ),
                    style = Stroke(width = r * 0.07f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = runnelPath,
                    color = Color.White.copy(alpha = 0.2f),
                    style = Stroke(width = r * 0.02f, cap = StrokeCap.Round)
                )
            }
        }
    }

    private fun DrawScope.drawStems(r: Float) {
        // Attachment point — pulled DOWN a bit so stems stay proportional
        val attachX = 0f
        val attachY = -1.05f * r

        // ============================================================
        // ONLY CHANGE: endpoints pulled inward to match new cherry cx (±0.48)
        // ============================================================
        val endpoints = listOf(
            Offset(-0.46f * r, -0.05f * r),  // left cherry top
            Offset(0.50f * r, -0.08f * r),   // right cherry top
            Offset(0.05f * r, 0.12f * r)     // center cherry top (unchanged)
        )

        // ============================================================
        // ONLY CHANGE: controls pulled inward so stems don't bow outward
        // ============================================================
        val controls = listOf(
            Offset(-0.46f * r, -0.6f * r),   // left, tighter bow
            Offset(0.50f * r, -0.65f * r),   // right, tighter bow
            Offset(0.15f * r, -0.5f * r)     // center, slightly less lean
        )

        endpoints.forEachIndexed { i, end ->
            val ctrl = controls[i]

            // Stem shadow
            val shadowStem = Path().apply {
                moveTo(attachX + r * 0.02f, attachY + r * 0.02f)
                quadraticBezierTo(
                    ctrl.x + r * 0.02f, ctrl.y + r * 0.02f,
                    end.x + r * 0.02f, end.y + r * 0.02f
                )
            }
            drawPath(
                path = shadowStem,
                color = Color(0xFF1B0000).copy(alpha = 0.35f),
                style = Stroke(width = r * 0.06f, cap = StrokeCap.Round)
            )

            // Stem main
            val stem = Path().apply {
                moveTo(attachX, attachY)
                quadraticBezierTo(ctrl.x, ctrl.y, end.x, end.y)
            }
            drawPath(
                path = stem,
                color = Color(0xFF4E342E),
                style = Stroke(width = r * 0.055f, cap = StrokeCap.Round)
            )

            // Stem highlight
            val stemHighlight = Path().apply {
                moveTo(attachX - r * 0.008f, attachY - r * 0.008f)
                quadraticBezierTo(
                    ctrl.x - r * 0.008f, ctrl.y - r * 0.008f,
                    end.x - r * 0.008f, end.y - r * 0.008f
                )
            }
            drawPath(
                path = stemHighlight,
                color = Color(0xFF8D6E63),
                style = Stroke(width = r * 0.018f, cap = StrokeCap.Round)
            )
        }

        // Knot (unchanged)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF8D6E63),
                    Color(0xFF4E342E),
                    Color(0xFF2D1B0E)
                ),
                center = Offset(attachX - r * 0.02f, attachY - r * 0.02f),
                radius = r * 0.12f
            ),
            radius = r * 0.09f,
            center = Offset(attachX, attachY)
        )
        drawCircle(
            color = Color(0xFFA1887F).copy(alpha = 0.6f),
            radius = r * 0.035f,
            center = Offset(attachX - r * 0.03f, attachY - r * 0.03f)
        )

        // Leaf (unchanged)
        drawLeaf(r, attachX, attachY)
    }

    private fun DrawScope.drawLeaf(r: Float, baseX: Float, baseY: Float) {
        val leafTipX = baseX + 0.55f * r
        val leafTipY = baseY - 0.35f * r

        val leaf = Path().apply {
            moveTo(baseX, baseY)
            cubicTo(
                baseX + 0.1f * r, baseY - 0.2f * r,
                baseX + 0.32f * r, baseY - 0.45f * r,
                leafTipX, leafTipY
            )
            cubicTo(
                baseX + 0.3f * r, baseY - 0.2f * r,
                baseX + 0.12f * r, baseY - 0.08f * r,
                baseX, baseY
            )
            close()
        }

        drawPath(
            path = leaf,
            color = Color(0xFF1B5E20).copy(alpha = 0.4f)
        )

        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF81C784),
                    Color(0xFF4CAF50),
                    Color(0xFF2E7D32),
                    Color(0xFF1B5E20)
                ),
                start = Offset(baseX, baseY),
                end = Offset(leafTipX, leafTipY)
            )
        )

        val vein = Path().apply {
            moveTo(baseX + 0.05f * r, baseY - 0.05f * r)
            quadraticBezierTo(
                baseX + 0.28f * r, baseY - 0.22f * r,
                leafTipX - 0.02f * r, leafTipY + 0.02f * r
            )
        }
        drawPath(
            path = vein,
            color = Color(0xFF1B5E20).copy(alpha = 0.65f),
            style = Stroke(width = r * 0.012f, cap = StrokeCap.Round)
        )

        for (i in 1..3) {
            val t = i / 4f
            val vx = baseX + (leafTipX - baseX) * t
            val vy = baseY + (leafTipY - baseY) * t
            val sideVein = Path().apply {
                moveTo(vx, vy)
                quadraticBezierTo(
                    vx + 0.02f * r, vy - 0.06f * r,
                    vx + 0.05f * r, vy - 0.1f * r
                )
            }
            drawPath(
                path = sideVein,
                color = Color(0xFF1B5E20).copy(alpha = 0.4f),
                style = Stroke(width = r * 0.008f, cap = StrokeCap.Round)
            )
        }

        drawPath(
            path = leaf,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                start = Offset(baseX + 0.08f * r, baseY - 0.25f * r),
                end = Offset(baseX + 0.3f * r, baseY - 0.1f * r)
            )
        )

        val dropX = baseX + 0.28f * r
        val dropY = baseY - 0.28f * r
        val dropR = 0.045f * r

        drawCircle(
            color = Color.Black.copy(alpha = 0.25f),
            radius = dropR * 1.1f,
            center = Offset(dropX + dropR * 0.3f, dropY + dropR * 0.4f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFE3F2FD).copy(alpha = 0.5f),
                    Color(0xFF90CAF9).copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(dropX - dropR * 0.3f, dropY - dropR * 0.3f),
                radius = dropR * 1.5f
            ),
            radius = dropR,
            center = Offset(dropX, dropY)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = dropR * 0.3f,
            center = Offset(dropX - dropR * 0.25f, dropY - dropR * 0.25f)
        )
    }
}