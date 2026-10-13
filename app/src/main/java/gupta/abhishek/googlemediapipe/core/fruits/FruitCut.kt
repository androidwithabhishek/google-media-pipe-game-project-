package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


// ---------------------------------------------------------------------
// CUT ANIMATION SETTINGS (change these numbers to tune the look)
// Distances are in pixels, times are in seconds.
// ---------------------------------------------------------------------
const val CUT_DURATION = 1.6f                // whole animation, then it is removed (also used by the game loop)
private const val DAGGER_TIME = 0.16f        // how long the dagger swing takes
private const val CUT_SPLIT_DELAY = 0.10f    // halves start moving after this time
private const val HALF_PUSH = 360f           // how fast the two halves move apart
private const val HALF_SPIN = 320f           // how fast the halves rotate (degrees per second)
private const val CUT_GRAVITY = 2400f        // how fast the halves fall down
private const val DROPLET_COUNT = 70         // small fast water drops
private const val BLOB_COUNT = 7             // big slow blobs of juice
private const val SPLAT_COUNT = 9            // juice stains that drip down the screen
private const val CHUNK_COUNT = 9            // flying pieces of fruit
private const val DROPLET_GRAVITY = 1800f    // how fast the drops fall down
private const val DROPLET_LIFE = 1.2f        // drops disappear after this time


// ---------------------------------------------------------------------
// Small helper classes. We only store how each thing STARTS; its position
// at any time is calculated from these numbers (so we never move it by hand).
// ---------------------------------------------------------------------

// One water drop. angle = flying direction (radians), speed = pixels per second,
// size = how big, offset = where along the cut line it starts.
class Droplet(val angle: Float, val speed: Float, val size: Float, val offset: Float)

// One juice stain on the screen (position is relative to the fruit center).
class Splat(val offsetX: Float, val offsetY: Float, val size: Float)

// One flying piece of fruit. spin = degrees per second, useSkin = skin color or flesh color.
class Chunk(val angle: Float, val speed: Float, val size: Float, val spin: Float, val useSkin: Boolean)


// ---------------------------------------------------------------------
// FruitCut
//   One running cut animation: dagger, two halves, water, stains, chunks.
//   "age" is the only thing that changes (seconds since the cut started).
//   The game loop adds time to age every frame, the drawing code does the rest.
//
//   type / centerX / centerY / radius -> which fruit, where, and how big it was
//   rotation -> how much the fruit was turned (degrees) when it was cut
// ---------------------------------------------------------------------
class FruitCut(
    val type: FruitType,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val rotation: Float
) {

    // Seconds since the cut started. Compose redraws when this changes.
    var age by mutableStateOf(0f)

    // Direction of the slash in radians: a diagonal between 25 and 65 degrees,
    // randomly going down-right or up-right.
    val angle: Float = run {
        val degrees = Random.nextInt(25, 66)
        val sign = if (Random.nextBoolean()) 1 else -1
        degrees * sign * (PI.toFloat() / 180f)
    }

    // Everything that sprays sideways out of the cut uses this direction (90 degrees to the cut),
    // to the left or to the right of the blade, with a random spread.
    private fun sprayAngle(): Float {
        val normalAngle = angle + (PI.toFloat() / 2f)
        val otherSide = if (Random.nextBoolean()) 0f else PI.toFloat()
        val wobble = (Random.nextFloat() - 0.5f) * 2.6f
        return normalAngle + otherSide + wobble
    }

    // Water: many small fast drops + a few big slow blobs (the last BLOB_COUNT items).
    val droplets: List<Droplet> = List(DROPLET_COUNT + BLOB_COUNT) { index ->
        val isBlob = index >= DROPLET_COUNT
        Droplet(
            angle = sprayAngle(),
            speed = if (isBlob) 120f + Random.nextFloat() * 260f else 200f + Random.nextFloat() * 750f,
            size = if (isBlob) radius * (0.14f + Random.nextFloat() * 0.08f)
            else radius * (0.04f + Random.nextFloat() * 0.08f),
            offset = (Random.nextFloat() * 2f - 1f) * radius
        )
    }

    // Juice stains around the fruit.
    val splats: List<Splat> = List(SPLAT_COUNT) {
        val a = Random.nextFloat() * 2f * PI.toFloat()
        val distance = radius * (0.3f + Random.nextFloat() * 1.9f)
        Splat(
            offsetX = cos(a) * distance,
            offsetY = sin(a) * distance,
            size = radius * (0.10f + Random.nextFloat() * 0.22f)
        )
    }

    // Flying pieces of fruit.
    val chunks: List<Chunk> = List(CHUNK_COUNT) {
        Chunk(
            angle = sprayAngle(),
            speed = 250f + Random.nextFloat() * 500f,
            size = radius * (0.10f + Random.nextFloat() * 0.14f),
            spin = (Random.nextFloat() - 0.5f) * 900f,
            useSkin = Random.nextFloat() < 0.35f
        )
    }
}


// ---------------------------------------------------------------------
// drawFruitCut
//   Paints the whole cut animation for the current age.
//   Order matters (later = on top):
//   stains, halves, splash rings, water drops, flying chunks, dagger.
// ---------------------------------------------------------------------
fun DrawScope.drawFruitCut(cut: FruitCut) {

    val t = cut.age
    val r = cut.radius
    val juice = cut.type.juiceColor
    val darkJuice = lerp(juice, Color.Black, 0.25f)     // darker edge so drops look like liquid

    // Direction of the cut line (dir).
    val dirX = cos(cut.angle)
    val dirY = sin(cut.angle)

    // Time since the dagger went through. Below 0 = nothing sprays yet.
    val dropTime = t - CUT_SPLIT_DELAY
    val lifeLeft = (1f - dropTime / (CUT_DURATION - CUT_SPLIT_DELAY)).coerceIn(0f, 1f)

    // ----- 1) Juice stains that slowly drip down -----
    if (dropTime > 0f) {
        val grow = (dropTime * 10f).coerceAtMost(1f)       // stains pop out quickly
        val stain = juice.copy(alpha = 0.55f * lifeLeft)

        for (s in cut.splats) {
            val c = Offset(cut.centerX + s.offsetX, cut.centerY + s.offsetY)

            // Main blob and two small satellite blobs, so it looks splashed and not round.
            drawCircle(color = stain, radius = s.size * grow, center = c)
            drawCircle(color = stain, radius = s.size * 0.35f * grow, center = c + Offset(s.size * 1.1f, -s.size * 0.3f))
            drawCircle(color = stain, radius = s.size * 0.30f * grow, center = c + Offset(-s.size * 1.0f, s.size * 0.4f))

            // The drip: a line that grows downward while the stain fades.
            drawLine(
                color = stain,
                start = c,
                end = c + Offset(0f, s.size * 3f * (1f - lifeLeft)),
                strokeWidth = s.size * 0.5f,
                cap = StrokeCap.Round
            )
        }
    }

    // ----- 2) The two fruit halves -----
    val splitTime = t - CUT_SPLIT_DELAY
    drawFruitHalf(cut, side = 1f, splitTime = splitTime.coerceAtLeast(0f))
    drawFruitHalf(cut, side = -1f, splitTime = splitTime.coerceAtLeast(0f))

    // ----- 3) Two splash rings that grow and fade -----
    for (i in 0..1) {
        val ringTime = t - i * 0.07f
        if (ringTime in 0f..0.4f) {
            val k = ringTime / 0.4f                        // goes from 0 to 1
            val eased = 1f - (1f - k) * (1f - k)           // starts fast, ends slow
            drawCircle(
                color = juice.copy(alpha = 0.7f * (1f - k)),
                radius = r * (0.4f + (1.2f + 0.8f * i) * eased),
                center = Offset(cut.centerX, cut.centerY),
                style = Stroke(width = (r * 0.16f * (1f - k)).coerceAtLeast(1f))
            )
        }
    }

    // ----- 3b) Water drops and big blobs -----
    if (dropTime > 0f) {
        val fade = (1f - dropTime / DROPLET_LIFE).coerceIn(0f, 1f)

        if (fade > 0f) {
            val tailTime = (dropTime - 0.04f).coerceAtLeast(0f)   // a moment ago = tail of the streak

            for (d in cut.droplets) {
                // Where the drop starts on the cut line.
                val startX = cut.centerX + dirX * d.offset
                val startY = cut.centerY + dirY * d.offset

                // Position now (flies straight + gravity pulls it down).
                val head = Offset(
                    startX + cos(d.angle) * d.speed * dropTime,
                    startY + sin(d.angle) * d.speed * dropTime + 0.5f * DROPLET_GRAVITY * dropTime * dropTime
                )

                // Position a moment ago.
                val tail = Offset(
                    startX + cos(d.angle) * d.speed * tailTime,
                    startY + sin(d.angle) * d.speed * tailTime + 0.5f * DROPLET_GRAVITY * tailTime * tailTime
                )

                // Dark streak, dark drop, lighter drop on top, tiny white glint = wet look.
                drawLine(
                    color = darkJuice.copy(alpha = fade),
                    start = tail,
                    end = head,
                    strokeWidth = d.size * 1.9f,
                    cap = StrokeCap.Round
                )
                drawCircle(color = darkJuice.copy(alpha = fade), radius = d.size, center = head)
                drawCircle(color = juice.copy(alpha = fade), radius = d.size * 0.75f, center = head)
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f * fade),
                    radius = d.size * 0.28f,
                    center = head + Offset(-d.size * 0.25f, -d.size * 0.25f)
                )
            }
        }
    }

    // ----- 3c) Flying pieces of fruit -----
    if (dropTime > 0f) {
        for (c in cut.chunks) {
            val x = cut.centerX + cos(c.angle) * c.speed * dropTime
            val y = cut.centerY + sin(c.angle) * c.speed * dropTime +
                    0.5f * CUT_GRAVITY * 0.7f * dropTime * dropTime

            val color = (if (c.useSkin) cut.type.skinColor else cut.type.insideColor).copy(alpha = lifeLeft)

            // Spin the little piece around its own center.
            rotate(degrees = c.spin * dropTime, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x - c.size / 2f, y - c.size / 2f),
                    size = Size(c.size, c.size * 0.7f),
                    cornerRadius = CornerRadius(c.size * 0.2f)
                )
            }
        }
    }

    // ----- 4) Slash trail and dagger -----
    val swipe = (t / DAGGER_TIME).coerceIn(0f, 1f)                       // 0 to 1 while swinging
    val daggerAlpha = (1f - (t - DAGGER_TIME) / 0.2f).coerceIn(0f, 1f)   // fades after the swing

    if (daggerAlpha > 0f) {
        val reach = r * 2.2f                         // dagger starts and ends this far from the center
        val here = (swipe * 2f - 1f) * reach         // goes from -reach to +reach

        val startX = cut.centerX - dirX * reach
        val startY = cut.centerY - dirY * reach
        val tipX = cut.centerX + dirX * here
        val tipY = cut.centerY + dirY * here

        // White slash line behind the dagger (thick glow + thin bright core).
        drawLine(
            color = Color.White.copy(alpha = 0.35f * daggerAlpha),
            start = Offset(startX, startY),
            end = Offset(tipX, tipY),
            strokeWidth = r * 0.22f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.95f * daggerAlpha),
            start = Offset(startX, startY),
            end = Offset(tipX, tipY),
            strokeWidth = r * 0.08f,
            cap = StrokeCap.Round
        )

        // The dagger itself: move to its tip, turn to the cut angle, draw.
        translate(tipX, tipY) {
            rotate(degrees = cut.angle * 180f / PI.toFloat(), pivot = Offset.Zero) {
                drawDagger(r, daggerAlpha)
            }
        }
    }
}


// ---------------------------------------------------------------------
// drawFruitHalf
//   Paints ONE half of the cut fruit.
//   side = +1 is one half, side = -1 is the other half.
//
//   Trick: we draw the whole fruit, but "clip" it so only the part on
//   one side of the cut line is visible. Then we move and spin that half.
// ---------------------------------------------------------------------
private fun DrawScope.drawFruitHalf(cut: FruitCut, side: Float, splitTime: Float) {

    val r = cut.radius

    val dirX = cos(cut.angle)
    val dirY = sin(cut.angle)
    val normX = -dirY
    val normY = dirX

    // How far this half moved: away from the cut + falling down with gravity.
    val moveX = side * normX * HALF_PUSH * splitTime
    val moveY = side * normY * HALF_PUSH * splitTime +
            0.5f * CUT_GRAVITY * splitTime * splitTime

    // How much this half turned (the two halves turn in opposite ways).
    val spin = side * HALF_SPIN * splitTime

    translate(cut.centerX + moveX, cut.centerY + moveY) {
        rotate(degrees = spin, pivot = Offset.Zero) {

            // The visible area: a big rectangle on one side of the cut line.
            val big = r * 3f
            val keepArea = Path().apply {
                moveTo(-dirX * big, -dirY * big)
                lineTo(dirX * big, dirY * big)
                lineTo(dirX * big + side * normX * big, dirY * big + side * normY * big)
                lineTo(-dirX * big + side * normX * big, -dirY * big + side * normY * big)
                close()
            }

            clipPath(keepArea) {

                // The whole fruit, turned like it was when it got cut
                // (only this half is visible because of the clip).
                rotate(degrees = cut.rotation, pivot = Offset.Zero) {
                    cut.type.draw(this, r)
                }

                // The cut face: skin colored edge first, flesh colored band on top of it.
                val half = r * 0.9f
                val faceStart = Offset(-dirX * half, -dirY * half)
                val faceEnd = Offset(dirX * half, dirY * half)
                drawLine(color = cut.type.skinColor, start = faceStart, end = faceEnd, strokeWidth = r * 0.40f)
                drawLine(color = cut.type.insideColor, start = faceStart, end = faceEnd, strokeWidth = r * 0.32f)

                // Seeds in a row just inside the cut face.
                val count = cut.type.seedCount
                for (i in 0 until count) {
                    val along = if (count == 1) 0f else (i / (count - 1f) - 0.5f) * r * 1.1f
                    drawCircle(
                        color = cut.type.seedColor,
                        radius = r * 0.045f,
                        center = Offset(
                            dirX * along + side * normX * r * 0.10f,
                            dirY * along + side * normY * r * 0.10f
                        )
                    )
                }
            }
        }
    }
}


// ---------------------------------------------------------------------
// drawDagger
//   Paints a small dagger. The tip is at (0, 0) and it points to the right,
//   so the caller only has to move and turn it.
//   alpha -> 1 = fully visible, 0 = invisible
// ---------------------------------------------------------------------
private fun DrawScope.drawDagger(r: Float, alpha: Float) {

    val bladeLength = r * 1.2f
    val bladeWidth = r * 0.16f

    // Blade: a long triangle, tip at (0, 0).
    val blade = Path().apply {
        moveTo(0f, 0f)
        lineTo(-bladeLength, -bladeWidth)
        lineTo(-bladeLength, bladeWidth)
        close()
    }
    drawPath(path = blade, color = Color(0xFFE0E0E0).copy(alpha = alpha))

    // Thin dark line down the middle of the blade.
    drawLine(
        color = Color(0xFF9E9E9E).copy(alpha = alpha),
        start = Offset(0f, 0f),
        end = Offset(-bladeLength, 0f),
        strokeWidth = r * 0.03f
    )

    // Gold cross guard (the bar between blade and handle).
    drawLine(
        color = Color(0xFFFFC107).copy(alpha = alpha),
        start = Offset(-bladeLength, -bladeWidth * 2f),
        end = Offset(-bladeLength, bladeWidth * 2f),
        strokeWidth = r * 0.1f,
        cap = StrokeCap.Round
    )

    // Brown handle.
    drawLine(
        color = Color(0xFF5D4037).copy(alpha = alpha),
        start = Offset(-bladeLength, 0f),
        end = Offset(-bladeLength - r * 0.5f, 0f),
        strokeWidth = r * 0.14f,
        cap = StrokeCap.Round
    )
}