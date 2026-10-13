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


// ---------------------------------------------------------------------
// FruitType
//   One kind of fruit.
//
//   name        -> just a label for you
//   skinColor   -> color of the outside (edge of the cut face + flying chunks)
//   juiceColor  -> color of the water drops and splashes
//   insideColor -> color of the flesh you see on the cut face
//   seedColor   -> color of the seeds on the cut face
//   seedCount   -> how many seeds show on the cut face (0 = none)
//   draw        -> paints the WHOLE fruit around (0, 0), "radius" = size
// ---------------------------------------------------------------------
class FruitType(
    val name: String,
    val skinColor: Color,
    val juiceColor: Color,
    val insideColor: Color,
    val seedColor: Color = Color(0xFF4E342E),
    val seedCount: Int = 3,
    val draw: DrawScope.(radius: Float) -> Unit
)


// ---------------------------------------------------------------------
// Fruits
//   The list of fruits in the game. Only the Apple is kept.
// ---------------------------------------------------------------------
object Fruits {

    // ----- APPLE: pale golden juice, cream flesh, small brown seeds -----
    val Apple = FruitType(
        name = "Apple",
        skinColor = Color(0xFFD32F2F),
        juiceColor = Color(0xFFFFECB3),
        insideColor = Color(0xFFFFF8E1),
        seedColor = Color(0xFF4E342E),
        seedCount = 3,
        draw = { radius -> drawApple(radius) }
    )

    // The game picks a random fruit from this list for every new fruit.
    val all: List<FruitType> = listOf(Apple)
}


// ---------------------------------------------------------------------
// drawApple
//   Glossy red apple: shaded body, top dimple, curved stem,
//   two-tone leaf with a midrib, soft shadow and highlights.
// ---------------------------------------------------------------------
private fun DrawScope.drawApple(r: Float) {

    // Soft ground shadow under the apple.
    drawOval(
        color = Color.Black.copy(alpha = 0.18f),
        topLeft = Offset(-0.8f * r, 0.88f * r),
        size = Size(1.6f * r, 0.26f * r)
    )

    // Apple body: wider shoulders, narrower bottom, dip at the base.
    val body = Path().apply {
        moveTo(0f, -0.62f * r)
        cubicTo(0.35f * r, -0.95f * r, 1.05f * r, -0.95f * r, 1.0f * r, -0.1f * r)
        cubicTo(0.98f * r, 0.6f * r, 0.6f * r, 1.0f * r, 0.28f * r, 0.9f * r)
        cubicTo(0.12f * r, 0.85f * r, 0.06f * r, 0.72f * r, 0f, 0.78f * r)
        cubicTo(-0.06f * r, 0.72f * r, -0.12f * r, 0.85f * r, -0.28f * r, 0.9f * r)
        cubicTo(-0.6f * r, 1.0f * r, -0.98f * r, 0.6f * r, -1.0f * r, -0.1f * r)
        cubicTo(-1.05f * r, -0.95f * r, -0.35f * r, -0.95f * r, 0f, -0.62f * r)
        close()
    }

    // Body fill: light highlight area fading to deep red at the edges.
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

    // Everything below stays inside the apple shape.
    clipPath(body) {
        // Warm rim light on the bottom-right for roundness.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(0.6f * r, 0.6f * r),
                radius = 0.5f * r
            ),
            radius = 0.5f * r,
            center = Offset(0.6f * r, 0.6f * r)
        )

        // Soft darker band near the top edge for depth.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0xFF7F0000).copy(alpha = 0.35f)),
                center = Offset(0f, 0f),
                radius = 1.1f * r
            ),
            radius = 1.1f * r,
            center = Offset.Zero
        )

        // Soft highlight (glossy shine) on the upper left.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.55f), Color.Transparent),
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

    // Curved stem.
    val stem = Path().apply {
        moveTo(0f, -0.68f * r)
        quadraticBezierTo(0.02f * r, -1.0f * r, 0.14f * r, -1.28f * r)
    }
    drawPath(
        path = stem,
        color = Color(0xFF4E342E),
        style = Stroke(width = 0.075f * r, cap = StrokeCap.Round)
    )

    // Leaf with a gradient from light to dark green.
    val leaf = Path().apply {
        moveTo(0.08f * r, -1.12f * r)
        quadraticBezierTo(0.45f * r, -1.58f * r, 0.92f * r, -1.32f * r)
        quadraticBezierTo(0.5f * r, -1.0f * r, 0.08f * r, -1.12f * r)
        close()
    }
    drawPath(
        path = leaf,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
            start = Offset(0.08f * r, -1.12f * r),
            end = Offset(0.92f * r, -1.32f * r)
        )
    )

    // Leaf midrib.
    drawLine(
        color = Color(0xFFC8E6C9).copy(alpha = 0.6f),
        start = Offset(0.14f * r, -1.13f * r),
        end = Offset(0.8f * r, -1.29f * r),
        strokeWidth = 0.02f * r,
        cap = StrokeCap.Round
    )

    // Sharp specular spot on top of the soft shine.
    rotate(degrees = -25f, pivot = Offset(-0.42f * r, -0.5f * r)) {
        drawOval(
            color = Color.White.copy(alpha = 0.7f),
            topLeft = Offset(-0.48f * r, -0.56f * r),
            size = Size(0.14f * r, 0.26f * r)
        )
    }
}