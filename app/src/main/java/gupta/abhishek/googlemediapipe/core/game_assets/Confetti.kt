package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CONFETTI_GRAVITY
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CONFETTI_PIECE_COUNT
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CONFETTI_SECONDS
import kotlin.random.Random

private val ConfettiColors = listOf(
    Color(0xFFFF5252), Color(0xFFFFD54F), Color(0xFF69F0AE),
    Color(0xFF40C4FF), Color(0xFFE040FB), Color(0xFFFFFFFF)
)

// One paper piece. Positions are fractions of the screen (0 to 1), so they fit any screen size.
private class ConfettiPiece {
    var x = 0.5f + (Random.nextFloat() - 0.5f) * 0.2f   // starts near the top center
    var y = 0.35f
    var vx = (Random.nextFloat() * 2f - 1f) * 0.7f      // sideways speed
    var vy = -(0.6f + Random.nextFloat() * 0.9f)        // first flies upward
    var angle = Random.nextFloat() * 360f
    var spin = (Random.nextFloat() * 2f - 1f) * 720f    // degrees per second
    val color: Color = ConfettiColors.random()
    val widthFraction = 0.012f + Random.nextFloat() * 0.010f
    val heightFraction = widthFraction * 2.2f

    fun update(dt: Float) {
        vy += CONFETTI_GRAVITY * dt                 // gravity pulls it down
        vx *= (1f - 0.6f * dt).coerceAtLeast(0f)    // air slows the sideways movement
        x += vx * dt
        y += vy * dt
        angle += spin * dt
    }
}

// ---------------------------------------------------------------------
// ConfettiLayer
//   Shows colored paper pieces falling from the air for CONFETTI_SECONDS.
//   Give it a new "trigger" number each time you want a burst.
//   Only the drawing layer updates each frame, not the whole screen.
// ---------------------------------------------------------------------
@Composable
internal fun ConfettiLayer(trigger: Int) {

    val pieces = remember { mutableListOf<ConfettiPiece>() }

    // Changing this number makes the confetti canvas redraw (one per frame while it runs).
    var frameTick by remember { mutableStateOf(0) }

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect

        pieces.clear()
        repeat(CONFETTI_PIECE_COUNT) { pieces.add(ConfettiPiece()) }

        var startNanos = -1L
        var lastNanos = -1L
        var running = true

        while (running) {
            withFrameNanos { now ->
                if (startNanos < 0L) {
                    startNanos = now
                    lastNanos = now
                }
                val dt = (now - lastNanos) / 1_000_000_000f
                lastNanos = now

                for (piece in pieces) piece.update(dt)
                frameTick++

                running = (now - startNanos) / 1_000_000_000f < CONFETTI_SECONDS
            }
        }

        pieces.clear()
        frameTick++
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Reading frameTick here makes this canvas redraw each time it changes.
        frameTick

        for (piece in pieces) {
            val cx = piece.x * size.width
            val cy = piece.y * size.height
            val w = piece.widthFraction * size.minDimension
            val h = piece.heightFraction * size.minDimension

            rotate(degrees = piece.angle, pivot = Offset(cx, cy)) {
                drawRect(
                    color = piece.color,
                    topLeft = Offset(cx - w / 2f, cy - h / 2f),
                    size = Size(w, h)
                )
            }
        }
    }
}
