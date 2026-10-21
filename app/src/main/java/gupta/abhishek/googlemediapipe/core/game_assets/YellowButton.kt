package gupta.abhishek.googlemediapipe.core.game_assets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate

// ---------------------------------------------------------------------
// The 3D looking yellow round button.
// Replay, Play and Pause all sit on top of this same button, so they match.
// It paints around the point (0, 0), and "r" is the radius of the button.
//
// Layers, from bottom to top:
//   1) soft shadow under the button
//   2) darker gold disc a bit lower = the "thickness" of the button
//   3) bright yellow ring (the bezel)
//   4) thin dark groove between the ring and the face
//   5) the face, with a gradient so it looks like a bulging dome
//   6) shiny highlights on the top
// ---------------------------------------------------------------------
fun DrawScope.drawYellowRoundButton(r: Float) {

    // 1) Shadow: a dark see-through circle, pushed down a little.
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = r,
        center = Offset(0.02f * r, 0.14f * r)
    )

    // 2) Thickness: a darker gold disc, lower than the ring. This is the "side" of the button.
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE09100), Color(0xFF8A4B00)),
            start = Offset(0f, -r),
            end = Offset(0f, r)
        ),
        radius = r,
        center = Offset(0f, 0.09f * r)
    )

    // 3) Bright yellow ring (light on top, orange at the bottom).
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF59D), Color(0xFFFFC400), Color(0xFFFF9800)),
            start = Offset(0f, -r),
            end = Offset(0f, r)
        ),
        radius = r,
        center = Offset.Zero
    )

    // 4) Thin dark groove where the face sits inside the ring.
    drawCircle(
        color = Color(0xFFC77800).copy(alpha = 0.8f),
        radius = 0.85f * r,
        center = Offset.Zero
    )

    // 5) The face: light yellow at the upper left, deeper gold at the lower right.
    val faceRadius = 0.80f * r
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFE135), Color(0xFFFFB300)),
            center = Offset(-0.25f * r, -0.35f * r),
            radius = 1.3f * r
        ),
        radius = faceRadius,
        center = Offset.Zero
    )

    // Thin orange edge around the face for depth.
    drawCircle(
        color = Color(0xFFFF8F00).copy(alpha = 0.5f),
        radius = faceRadius,
        center = Offset.Zero,
        style = Stroke(width = 0.04f * r)
    )

    // 6) Shiny highlight: a white oval on the upper half of the face (clipped to the face circle).
    val facePath = Path().apply {
        addOval(Rect(center = Offset.Zero, radius = faceRadius))
    }
    clipPath(facePath) {
        drawOval(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.05f)),
                start = Offset(0f, -0.8f * r),
                end = Offset(0f, -0.05f * r)
            ),
            topLeft = Offset(-0.65f * r, -0.80f * r),
            size = Size(1.3f * r, 0.75f * r)
        )
    }

    // A short bright arc on the ring, upper left, like light hitting a rounded edge.
    drawArc(
        color = Color.White.copy(alpha = 0.6f),
        startAngle = 200f,
        sweepAngle = 70f,
        useCenter = false,
        topLeft = Offset(-0.92f * r, -0.92f * r),
        size = Size(1.84f * r, 1.84f * r),
        style = Stroke(width = 0.06f * r, cap = StrokeCap.Round)
    )
}

// ---------------------------------------------------------------------
// engraved
//   Makes an icon look pressed into the yellow button.
//   It runs your drawing code TWICE:
//     first a light copy, moved a tiny bit down and right (the shiny edge),
//     then the real dark brown copy on top.
//   Your drawing code receives the color to use.
// ---------------------------------------------------------------------
internal fun DrawScope.engraved(r: Float, draw: DrawScope.(Color) -> Unit) {

    // Light copy, a little lower and to the right.
    translate(0.02f * r, 0.045f * r) {
        draw(Color.White.copy(alpha = 0.65f))
    }

    // Dark brown copy on top.
    draw(Color(0xFF6A3500))
}
