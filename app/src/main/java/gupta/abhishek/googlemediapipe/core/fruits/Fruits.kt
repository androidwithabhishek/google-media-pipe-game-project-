package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


// ---------------------------------------------------------------------
// FruitType
//   One kind of fruit. To add a new fruit you only need to:
//     1) write a draw function like drawApple() below
//     2) add one FruitType(...) inside the Fruits object
//     3) put it in the Fruits.all list
//   The game and the cutting animation pick it up automatically.
//
//   name        -> just a label for you
//   skinColor   -> color of the outside (shows as the edge of the cut face + flying chunks)
//   juiceColor  -> color of the water drops and splashes (match the real juice!)
//   insideColor -> color of the flesh you see on the cut face
//   seedColor   -> color of the seeds on the cut face
//   seedCount   -> how many seeds show on the cut face (0 = none)
//   draw        -> function that paints the WHOLE fruit.
//                  It paints around the point (0, 0), so (0, 0) is the fruit center,
//                  and "radius" is how big the fruit is.
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
//   The list of all fruits in the game.
// ---------------------------------------------------------------------
object Fruits {

    // ----- APPLE: pale golden juice, cream flesh, small brown seeds -----
    val Apple = FruitType(
        name = "Apple",
        skinColor = Color(0xFFC62828),
        juiceColor = Color(0xFFFFECB3),
        insideColor = Color(0xFFFFF8E1),
        draw = { radius -> drawApple(radius) }
    )

    // ----- ORANGE: bright orange juice, orange flesh, no seeds -----
    val Orange = FruitType(
        name = "Orange",
        skinColor = Color(0xFFEF6C00),
        juiceColor = Color(0xFFFFA726),
        insideColor = Color(0xFFFFB74D),
        seedCount = 0,
        draw = { radius -> drawOrange(radius) }
    )

    // ----- WATERMELON: red juice, red flesh, black seeds, green rind -----
    val Watermelon = FruitType(
        name = "Watermelon",
        skinColor = Color(0xFF2E7D32),
        juiceColor = Color(0xFFFF5252),
        insideColor = Color(0xFFFF6F61),
        seedColor = Color(0xFF212121),
        seedCount = 5,
        draw = { radius -> drawWatermelon(radius) }
    )

    // ----- LEMON: pale yellow juice, light yellow flesh -----
    val Lemon = FruitType(
        name = "Lemon",
        skinColor = Color(0xFFF9A825),
        juiceColor = Color(0xFFFFF59D),
        insideColor = Color(0xFFFFF9C4),
        seedColor = Color(0xFFFFF3C4),
        seedCount = 2,
        draw = { radius -> drawLemon(radius) }
    )

    // ----- KIWI: green juice, green flesh, black seeds, brown fuzzy skin -----
    val Kiwi = FruitType(
        name = "Kiwi",
        skinColor = Color(0xFF6D4C41),
        juiceColor = Color(0xFFAED581),
        insideColor = Color(0xFF9CCC65),
        seedColor = Color(0xFF212121),
        seedCount = 5,
        draw = { radius -> drawKiwi(radius) }
    )

    // ----- STRAWBERRY: pink-red juice, red flesh, tiny seeds are on the outside -----
    val Strawberry = FruitType(
        name = "Strawberry",
        skinColor = Color(0xFFD32F2F),
        juiceColor = Color(0xFFEF5350),
        insideColor = Color(0xFFFF8A80),
        seedCount = 0,
        draw = { radius -> drawStrawberry(radius) }
    )

    // ----- ADD NEW FRUITS HERE -----

    // The game picks a random fruit from this list for every new fruit.
    // When you make a new fruit, add it here: listOf(Apple, Orange, ..., MyNewFruit)
    val all: List<FruitType> = listOf(Apple, Orange, Watermelon, Lemon, Kiwi, Strawberry)
}


// ---------------------------------------------------------------------
// drawApple
//   Dark red edge, red body, dent, stem, green leaf and a white shine.
// ---------------------------------------------------------------------
private fun DrawScope.drawApple(r: Float) {

    val red = Color(0xFFD32F2F)
    val darkRed = Color(0xFFB71C1C)
    val brown = Color(0xFF5D4037)
    val green = Color(0xFF43A047)

    // Dark circle first, slightly bigger = gives the apple an outline.
    drawCircle(color = darkRed, radius = r, center = Offset.Zero)

    // Red body on top of it.
    drawCircle(color = red, radius = r * 0.94f, center = Offset(0f, -r * 0.02f))

    // Small dent at the top where the stem sits.
    drawCircle(color = darkRed, radius = r * 0.16f, center = Offset(0f, -r * 0.92f))

    // Stem.
    drawLine(
        color = brown,
        start = Offset(0f, -r * 0.85f),
        end = Offset(r * 0.08f, -r * 1.25f),
        strokeWidth = r * 0.09f,
        cap = StrokeCap.Round
    )

    // Leaf: two curves that meet at both ends.
    val leaf = Path().apply {
        moveTo(r * 0.08f, -r * 1.12f)
        quadraticBezierTo(r * 0.45f, -r * 1.50f, r * 0.80f, -r * 1.20f)
        quadraticBezierTo(r * 0.45f, -r * 1.00f, r * 0.08f, -r * 1.12f)
        close()
    }
    drawPath(path = leaf, color = green)

    // White shine (see-through) on the upper left.
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = r * 0.2f,
        center = Offset(-r * 0.45f, -r * 0.35f)
    )
}


// ---------------------------------------------------------------------
// drawOrange
//   Orange body with tiny pores, a green button and a leaf.
// ---------------------------------------------------------------------
private fun DrawScope.drawOrange(r: Float) {

    // Outline and body.
    drawCircle(color = Color(0xFFE65100), radius = r, center = Offset.Zero)
    drawCircle(color = Color(0xFFFB8C00), radius = r * 0.95f, center = Offset(0f, -r * 0.02f))

    // Pores: small light dots spread in a spiral (2.4 is the "golden angle", looks natural).
    for (i in 0 until 16) {
        val angle = i * 2.4f
        val distance = r * 0.8f * sqrt(i / 16f)
        drawCircle(
            color = Color(0xFFFFB74D).copy(alpha = 0.6f),
            radius = r * 0.035f,
            center = Offset(cos(angle) * distance, sin(angle) * distance)
        )
    }

    // Green button where the orange was attached.
    drawCircle(color = Color(0xFF558B2F), radius = r * 0.12f, center = Offset(0f, -r * 0.9f))

    // Small leaf.
    val leaf = Path().apply {
        moveTo(0f, -r * 0.92f)
        quadraticBezierTo(r * 0.35f, -r * 1.30f, r * 0.70f, -r * 1.00f)
        quadraticBezierTo(r * 0.35f, -r * 0.85f, 0f, -r * 0.92f)
        close()
    }
    drawPath(path = leaf, color = Color(0xFF66BB6A))

    // Shine.
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = r * 0.2f,
        center = Offset(-r * 0.45f, -r * 0.35f)
    )
}


// ---------------------------------------------------------------------
// drawWatermelon
//   Green ball with darker stripes and a short stem.
// ---------------------------------------------------------------------
private fun DrawScope.drawWatermelon(r: Float) {

    // Dark outline and light green body.
    drawCircle(color = Color(0xFF1B5E20), radius = r, center = Offset.Zero)
    drawCircle(color = Color(0xFF388E3C), radius = r * 0.95f, center = Offset.Zero)

    // Dark stripes. clipPath keeps them inside the ball.
    val body = Path().apply {
        addOval(Rect(center = Offset.Zero, radius = r * 0.95f))
    }
    clipPath(body) {
        for (i in -3..3) {
            drawLine(
                color = Color(0xFF1B5E20).copy(alpha = 0.75f),
                start = Offset(i * r * 0.3f, -r),
                end = Offset(i * r * 0.3f + r * 0.1f, r),
                strokeWidth = r * 0.1f
            )
        }
    }

    // Short stem.
    drawLine(
        color = Color(0xFF5D4037),
        start = Offset(0f, -r * 0.9f),
        end = Offset(0f, -r * 1.15f),
        strokeWidth = r * 0.1f,
        cap = StrokeCap.Round
    )

    // Shine.
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = r * 0.18f,
        center = Offset(-r * 0.45f, -r * 0.4f)
    )
}


// ---------------------------------------------------------------------
// drawLemon
//   Yellow oval with a small point on each end and a leaf.
// ---------------------------------------------------------------------
private fun DrawScope.drawLemon(r: Float) {

    val yellow = Color(0xFFFDD835)
    val edge = Color(0xFFF9A825)

    // Outline oval, then the lighter oval on top.
    drawOval(color = edge, topLeft = Offset(-r, -r * 0.9f), size = Size(r * 2f, r * 1.8f))
    drawOval(color = yellow, topLeft = Offset(-r * 0.95f, -r * 0.85f), size = Size(r * 1.9f, r * 1.7f))

    // The two small points at both ends.
    drawCircle(color = edge, radius = r * 0.14f, center = Offset(-r * 0.98f, 0f))
    drawCircle(color = edge, radius = r * 0.14f, center = Offset(r * 0.98f, 0f))

    // Small leaf at the top.
    val leaf = Path().apply {
        moveTo(0f, -r * 0.84f)
        quadraticBezierTo(r * 0.3f, -r * 1.20f, r * 0.62f, -r * 0.95f)
        quadraticBezierTo(r * 0.3f, -r * 0.80f, 0f, -r * 0.84f)
        close()
    }
    drawPath(path = leaf, color = Color(0xFF7CB342))

    // Shine.
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = r * 0.18f,
        center = Offset(-r * 0.4f, -r * 0.35f)
    )
}


// ---------------------------------------------------------------------
// drawKiwi
//   Brown fuzzy ball.
// ---------------------------------------------------------------------
private fun DrawScope.drawKiwi(r: Float) {

    // Outline and body.
    drawCircle(color = Color(0xFF5D4037), radius = r, center = Offset.Zero)
    drawCircle(color = Color(0xFF8D6E63), radius = r * 0.94f, center = Offset.Zero)

    // Fuzz: small dark dots spread in a spiral.
    for (i in 0 until 24) {
        val angle = i * 2.4f
        val distance = r * 0.85f * sqrt(i / 24f)
        drawCircle(
            color = Color(0xFF5D4037).copy(alpha = 0.5f),
            radius = r * 0.03f,
            center = Offset(cos(angle) * distance, sin(angle) * distance)
        )
    }

    // Small nub at the top.
    drawCircle(color = Color(0xFF4E342E), radius = r * 0.1f, center = Offset(0f, -r * 0.95f))

    // Light patch (like fuzz catching the light).
    drawCircle(
        color = Color.White.copy(alpha = 0.18f),
        radius = r * 0.22f,
        center = Offset(-r * 0.4f, -r * 0.35f)
    )
}


// ---------------------------------------------------------------------
// drawStrawberry
//   Rounded top with a point at the bottom, yellow seeds, green leaves.
// ---------------------------------------------------------------------
private fun DrawScope.drawStrawberry(r: Float) {

    val red = Color(0xFFE53935)
    val darkRed = Color(0xFFB71C1C)

    // Dark outline: a circle on top + a triangle pointing down.
    drawCircle(color = darkRed, radius = r * 0.98f, center = Offset(0f, -r * 0.12f))
    val outerCone = Path().apply {
        moveTo(-r * 0.92f, 0f)
        lineTo(r * 0.92f, 0f)
        lineTo(0f, r * 1.1f)
        close()
    }
    drawPath(path = outerCone, color = darkRed)

    // Red body, a little smaller so the dark edge stays visible.
    drawCircle(color = red, radius = r * 0.9f, center = Offset(0f, -r * 0.12f))
    val innerCone = Path().apply {
        moveTo(-r * 0.84f, 0f)
        lineTo(r * 0.84f, 0f)
        lineTo(0f, r * 1.02f)
        close()
    }
    drawPath(path = innerCone, color = red)

    // Seeds on the outside (small yellow dots in a spiral).
    for (i in 0 until 14) {
        val angle = i * 2.4f
        val distance = r * 0.75f * sqrt(i / 14f)
        drawCircle(
            color = Color(0xFFFFEB3B),
            radius = r * 0.04f,
            center = Offset(cos(angle) * distance, sin(angle) * distance * 0.9f + r * 0.1f)
        )
    }

    // Green leaves: five small triangles around the top.
    for (i in -2..2) {
        val leaf = Path().apply {
            moveTo(0f, -r * 0.95f)
            lineTo(i * r * 0.4f - r * 0.12f, -r * 1.15f + abs(i) * r * 0.1f)
            lineTo(i * r * 0.4f + r * 0.12f, -r * 1.10f + abs(i) * r * 0.1f)
            close()
        }
        drawPath(path = leaf, color = Color(0xFF43A047))
    }

    // Shine.
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = r * 0.16f,
        center = Offset(-r * 0.45f, -r * 0.4f)
    )
}