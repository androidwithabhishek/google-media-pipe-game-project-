package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random


// ---------------------------------------------------------------------
// Small helper classes. We only store how each piece STARTS; its position
// at any time is calculated from these numbers.
// ---------------------------------------------------------------------

// A ball of fire. angle = direction, speed = launch speed (pixels per second),
// size = radius, lifeScale = how long it lives compared to the others.
class FireParticle(val angle: Float, val speed: Float, val size: Float, val lifeScale: Float)

// A thin bright spark that shoots out fast. width = line thickness.
class Spark(val angle: Float, val speed: Float, val width: Float)

// A puff of dark smoke. Offsets are in pixels from the blast center,
// size is relative to the blast size, rise = how far it floats up.
class Smoke(val offsetX: Float, val offsetY: Float, val size: Float, val rise: Float)


// ---------------------------------------------------------------------
// Blast
//   One running explosion. "age" is the only thing that changes
//   (seconds since the bomb was cut). The game loop adds time to it.
//
//   bomb       -> which bomb exploded (decides how big and violent it is)
//   centerX/Y  -> where it exploded
//   bombRadius -> size of the bomb in pixels
// ---------------------------------------------------------------------
class Blast(
    val bomb: BombType,
    val centerX: Float,
    val centerY: Float,
    val bombRadius: Float
) {

    // Seconds since the explosion started. Compose redraws when this changes.
    var age by mutableStateOf(0f)

    // How far the explosion reaches, in pixels.
    val reach: Float = bombRadius * bomb.blastScale

    // How long it lasts.
    val duration: Float = bomb.blastSeconds

    // Balls of fire flying out in all directions.
    // They slow down because of "drag", so final distance = speed / 3.2.
    val fire: List<FireParticle> = List(bomb.particleCount) {
        FireParticle(
            angle = Random.nextFloat() * 2f * PI.toFloat(),
            speed = reach * 3.2f * (0.3f + Random.nextFloat() * 0.8f),
            size = bombRadius * (0.12f + Random.nextFloat() * 0.30f),
            lifeScale = 0.6f + Random.nextFloat() * 0.5f
        )
    }

    // Fast sparks, half as many as fire balls.
    val sparks: List<Spark> = List(bomb.particleCount / 2) {
        Spark(
            angle = Random.nextFloat() * 2f * PI.toFloat(),
            speed = reach * 2.2f * (0.6f + Random.nextFloat()),
            width = bombRadius * (0.03f + Random.nextFloat() * 0.05f)
        )
    }

    // Smoke clouds.
    val smoke: List<Smoke> = List(14) {
        Smoke(
            offsetX = (Random.nextFloat() - 0.5f) * reach * 0.7f,
            offsetY = (Random.nextFloat() - 0.5f) * reach * 0.7f,
            size = 0.25f + Random.nextFloat() * 0.2f,
            rise = reach * (0.2f + Random.nextFloat() * 0.4f)
        )
    }
}


// ---------------------------------------------------------------------
// drawBlast
//   Paints the whole explosion for the current age.
//   Order (later = on top): smoke, fireball, fire balls, sparks,
//   shock rings, screen glow, white flash.
// ---------------------------------------------------------------------
fun DrawScope.drawBlast(blast: Blast) {

    val t = blast.age
    val center = Offset(blast.centerX, blast.centerY)
    val reach = blast.reach
    val duration = blast.duration
    val bomb = blast.bomb

    // ----- 1) Smoke: dark clouds that grow and float up -----
    val smokeDelay = 0.1f
    val smokeTime = t - smokeDelay
    if (smokeTime > 0f) {
        val life = (smokeTime / (duration - smokeDelay)).coerceIn(0f, 1f)
        val fadeIn = (smokeTime * 6f).coerceAtMost(1f)

        for (s in blast.smoke) {
            drawCircle(
                color = Color(0xFF424242).copy(alpha = 0.55f * (1f - life) * fadeIn),
                radius = reach * s.size * (0.4f + 0.8f * life),
                center = Offset(
                    center.x + s.offsetX * (0.5f + life),
                    center.y + s.offsetY * (0.5f + life) - s.rise * life
                )
            )
        }
    }

    // ----- 2) Fireball: glowing core that blows up fast (white > yellow > orange > red) -----
    val fireballTime = 0.4f
    if (t < fireballTime) {
        val k = t / fireballTime                              // 0 to 1
        val radius = (reach * 0.6f * (0.2f + 0.8f * k)).coerceAtLeast(1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFFFEB3B),
                    Color(0xFFFF9800),
                    Color(0xFFD84315).copy(alpha = 0f)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
            alpha = 1f - k * 0.7f
        )
    }

    // ----- 3) Fire balls: fly out fast, slow down, change color as they cool -----
    val drag = 3.2f
    for (p in blast.fire) {
        val life = (t / (duration * 0.75f * p.lifeScale)).coerceIn(0f, 1f)
        if (life >= 1f) continue

        // Distance flown so far (fast at the start, then slower and slower).
        val distance = p.speed * (1f - exp(-drag * t)) / drag

        // Color: yellow -> orange -> red -> dark gray.
        val color = when {
            life < 0.25f -> lerp(Color(0xFFFFEB3B), Color(0xFFFF9800), life / 0.25f)
            life < 0.60f -> lerp(Color(0xFFFF9800), Color(0xFFD50000), (life - 0.25f) / 0.35f)
            else -> lerp(Color(0xFFD50000), Color(0xFF424242), (life - 0.60f) / 0.40f)
        }

        drawCircle(
            color = color.copy(alpha = 1f - life * life),
            radius = p.size * (1.1f - life * 0.6f),
            center = Offset(
                center.x + cos(p.angle) * distance,
                center.y + sin(p.angle) * distance + 120f * t * t     // tiny bit of gravity
            )
        )
    }

    // ----- 4) Sparks: thin bright streaks -----
    val sparkDrag = 2.2f
    val sparkLife = (1f - t / (duration * 0.5f)).coerceIn(0f, 1f)
    if (sparkLife > 0f) {
        val tailTime = (t - 0.05f).coerceAtLeast(0f)       // a moment ago = tail of the streak

        for (s in blast.sparks) {
            val distanceNow = s.speed * (1f - exp(-sparkDrag * t)) / sparkDrag
            val distanceTail = s.speed * (1f - exp(-sparkDrag * tailTime)) / sparkDrag

            drawLine(
                color = Color(0xFFFFF59D).copy(alpha = sparkLife),
                start = Offset(center.x + cos(s.angle) * distanceTail, center.y + sin(s.angle) * distanceTail),
                end = Offset(center.x + cos(s.angle) * distanceNow, center.y + sin(s.angle) * distanceNow),
                strokeWidth = s.width,
                cap = StrokeCap.Round
            )
        }
    }

    // ----- 5) Shock rings: two rings pushed out by the blast -----
    for (i in 0..1) {
        val ringTime = t - i * 0.08f
        if (ringTime in 0f..0.6f) {
            val k = ringTime / 0.6f
            val eased = 1f - (1f - k) * (1f - k)           // starts fast, ends slow
            drawCircle(
                color = bomb.flashColor.copy(alpha = 0.8f * (1f - k)),
                radius = reach * (0.3f + 1.3f * eased),
                center = center,
                style = Stroke(width = (blast.bombRadius * 0.35f * (1f - k)).coerceAtLeast(2f))
            )
        }
    }

    // Both screen effects are drawn a bit bigger than the screen,
    // so the camera shake never shows an uncovered edge.
    val topLeft = Offset(-200f, -200f)
    val fullSize = Size(size.width + 400f, size.height + 400f)

    // ----- 6) Screen glow: the whole screen turns orange or red for a moment -----
    val tintTime = duration * 0.6f
    if (t < tintTime) {
        drawRect(
            color = bomb.tintColor.copy(alpha = bomb.tintAlpha * (1f - t / tintTime)),
            topLeft = topLeft,
            size = fullSize
        )
    }

    // ----- 7) White-hot flash over everything for the first moment -----
    val flashTime = if (bomb.blastScale > 6f) 0.45f else 0.25f
    if (t < flashTime) {
        drawRect(
            color = bomb.flashColor.copy(alpha = 0.95f * (1f - t / flashTime)),
            topLeft = topLeft,
            size = fullSize
        )
    }
}