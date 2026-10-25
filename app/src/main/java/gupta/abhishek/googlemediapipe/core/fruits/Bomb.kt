package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SMALL_BOMB_LIVES_LOST
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.SMALL_BOMB_SCORE_PENALTY


// ---------------------------------------------------------------------
// BombType
//   One kind of bomb. Every number here decides how it looks and how
//   hard the explosion hits. To make a new bomb: add a BombType inside
//   the Bombs object below and use it in Bombs.random().
//
//   sizeScale     -> size compared to a fruit (1 = same size, 1.4 = 40% bigger)
//   bodyColor     -> color of the bomb ball
//   glowColor     -> color of the warning glow around the bomb
//   hasSkull      -> draws a white skull on the bomb
//   livesLost     -> lives you lose when you cut it (99 = game over for sure)
//   blastScale    -> size of the explosion, in bomb sizes (bigger = fills more screen)
//   blastSeconds  -> how long the explosion lasts
//   particleCount -> how many fire balls fly out (sparks and smoke scale with it)
//   shakePower    -> how hard the camera shakes, in pixels
//   shakeSeconds  -> how long the camera shakes
//   flashColor    -> color of the first white-hot flash over the screen
//   tintColor     -> color the screen glows for a moment after the flash
//   tintAlpha     -> how strong that glow is (0 = none, 1 = solid)
// ---------------------------------------------------------------------
class BombType(
    val name: String,
    val sizeScale: Float,
    val bodyColor: Color,
    val glowColor: Color,
    val hasSkull: Boolean,
    val livesLost: Int,
    val blastScale: Float,
    val blastSeconds: Float,
    val particleCount: Int,
    val shakePower: Float,
    val shakeSeconds: Float,
    val flashColor: Color,
    val tintColor: Color,
    val tintAlpha: Float,
    val scorePenalty: Int = 0          // points taken away when you cut this bomb (0 = none)
)


// ---------------------------------------------------------------------
// Bombs
//   The two bombs in the game.
// ---------------------------------------------------------------------
object Bombs {

    // ----- NORMAL BOMB: black, medium explosion, you lose 2 lives -----
    val Normal = BombType(
        name = "Bomb",
        sizeScale = 1.1f,
        bodyColor = Color(0xFF212121),
        glowColor = Color(0xFFFF5722),
        hasSkull = false,
        livesLost = SMALL_BOMB_LIVES_LOST,
        scorePenalty = SMALL_BOMB_SCORE_PENALTY,
        blastScale = 3.5f,
        blastSeconds = 1.4f,
        particleCount = 60,
        shakePower = 30f,
        shakeSeconds = 0.6f,
        flashColor = Color(0xFFFFF3E0),
        tintColor = Color(0xFFFF6D00),
        tintAlpha = 0.30f
    )

    // ----- MEGA BOMB: dark red with a skull, HUGE explosion, instant game over -----
    val Mega = BombType(
        name = "Mega Bomb",
        sizeScale = 1.4f,
        bodyColor = Color(0xFF5D0000),
        glowColor = Color(0xFFFF1744),
        hasSkull = true,
        livesLost = 99,
        blastScale = 8f,
        blastSeconds = 2.2f,
        particleCount = 170,
        shakePower = 70f,
        shakeSeconds = 1.2f,
        flashColor = Color.White,
        tintColor = Color(0xFFD50000),
        tintAlpha = 0.5f
    )

    // Picks a bomb. megaChance = 0.25 means one bomb in four is the mega bomb.
    fun random(megaChance: Float = 0.25f): BombType {
        return if (Random.nextFloat() < megaChance) Mega else Normal
    }
}


// ---------------------------------------------------------------------
// drawBomb
//   Paints a bomb around (0, 0): glowing warning ring, round body, shine,
//   neck, a curly fuse with a flickering spark and (for the mega bomb) a skull.
//
//   r    -> bomb radius in pixels
//   time -> any number that keeps growing (we pass the spin angle).
//           It makes the glow pulse and the spark flicker.
// ---------------------------------------------------------------------
fun DrawScope.drawBomb(bomb: BombType, r: Float, time: Float) {

    // Glow that slowly pulses, so you notice the bomb among the fruits.
    val pulse = 0.5f + 0.5f * sin(time * 0.1f)
    drawCircle(
        color = bomb.glowColor.copy(alpha = 0.18f + 0.18f * pulse),
        radius = r * (1.30f + 0.12f * pulse),
        center = Offset.Zero
    )

    // Body and shine.
    drawCircle(color = bomb.bodyColor, radius = r, center = Offset.Zero)
    drawCircle(
        color = Color.White.copy(alpha = 0.22f),
        radius = r * 0.2f,
        center = Offset(-r * 0.38f, -r * 0.40f)
    )

    // Neck: the small metal cap at the top.
    drawRect(
        color = Color(0xFF546E7A),
        topLeft = Offset(-r * 0.2f, -r * 1.12f),
        size = Size(r * 0.4f, r * 0.3f)
    )

    // Fuse: a brown curved line from the neck going up and to the right.
    val fuse = Path().apply {
        moveTo(0f, -r * 1.1f)
        quadraticBezierTo(r * 0.15f, -r * 1.45f, r * 0.55f, -r * 1.5f)
    }
    drawPath(
        path = fuse,
        color = Color(0xFF8D6E63),
        style = Stroke(width = r * 0.09f, cap = StrokeCap.Round)
    )

    // Spark at the end of the fuse. It flickers because of "time".
    val sparkCenter = Offset(r * 0.55f, -r * 1.5f)
    val flicker = 0.14f + 0.05f * sin(time * 0.35f)
    drawCircle(color = Color(0xFFFF9800).copy(alpha = 0.5f), radius = r * flicker * 2.2f, center = sparkCenter)
    drawCircle(color = Color(0xFFFFEB3B), radius = r * flicker, center = sparkCenter)
    drawCircle(color = Color.White, radius = r * flicker * 0.5f, center = sparkCenter)

    // Six little rays around the spark that turn around.
    for (i in 0 until 6) {
        val rayAngle = time * 0.2f + i * 1.047f          // 1.047 radians = 60 degrees
        val rayLength = r * 0.22f * (0.6f + 0.4f * sin(time * 0.3f + i))
        drawLine(
            color = Color(0xFFFFEB3B),
            start = sparkCenter,
            end = sparkCenter + Offset(cos(rayAngle) * rayLength, sin(rayAngle) * rayLength),
            strokeWidth = r * 0.03f,
            cap = StrokeCap.Round
        )
    }

    // Skull for the mega bomb: white head and jaw, black eyes, nose and teeth.
    if (bomb.hasSkull) {
        drawCircle(color = Color(0xFFF5F5F5), radius = r * 0.45f, center = Offset(0f, -r * 0.08f))
        drawRoundRect(
            color = Color(0xFFF5F5F5),
            topLeft = Offset(-r * 0.22f, r * 0.2f),
            size = Size(r * 0.44f, r * 0.3f),
            cornerRadius = CornerRadius(r * 0.08f)
        )
        drawCircle(color = Color.Black, radius = r * 0.12f, center = Offset(-r * 0.17f, -r * 0.1f))
        drawCircle(color = Color.Black, radius = r * 0.12f, center = Offset(r * 0.17f, -r * 0.1f))

        val nose = Path().apply {
            moveTo(0f, r * 0.03f)
            lineTo(-r * 0.06f, r * 0.15f)
            lineTo(r * 0.06f, r * 0.15f)
            close()
        }
        drawPath(path = nose, color = Color.Black)

        for (i in -1..1) {
            drawLine(
                color = Color.Black,
                start = Offset(i * r * 0.1f, r * 0.26f),
                end = Offset(i * r * 0.1f, r * 0.46f),
                strokeWidth = r * 0.025f
            )
        }
    }
}