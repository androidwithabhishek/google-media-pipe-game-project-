package gupta.abhishek.googlemediapipe.core.game_assets


import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath


fun DrawScope.drawYellowSquareButton(r: Float) {

    // r = half the button's side. Outer corner roundness.
    val outerCorner = 0.38f * r

    // Keeps corners "concentric": a smaller inset square gets a smaller corner.
    fun cornerFor(half: Float) = (outerCorner - (r - half)).coerceAtLeast(0f)

    // 1) Shadow: dark see-through rounded square, pushed down a little.
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.35f),
        topLeft = Offset(-r + 0.02f * r, -r + 0.14f * r),
        size = Size(2f * r, 2f * r),
        cornerRadius = CornerRadius(outerCorner)
    )

    // 2) Thickness: darker gold, lower than the ring (the "side" of the button).
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE09100), Color(0xFF8A4B00)),
            start = Offset(0f, -r),
            end = Offset(0f, r)
        ),
        topLeft = Offset(-r, -r + 0.09f * r),
        size = Size(2f * r, 2f * r),
        cornerRadius = CornerRadius(outerCorner)
    )

    // 3) Bright yellow ring (light on top, orange at the bottom).
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF59D), Color(0xFFFFC400), Color(0xFFFF9800)),
            start = Offset(0f, -r),
            end = Offset(0f, r)
        ),
        topLeft = Offset(-r, -r),
        size = Size(2f * r, 2f * r),
        cornerRadius = CornerRadius(outerCorner)
    )

    // 4) Thin dark groove where the face sits inside the ring.
    val grooveHalf = 0.85f * r
    drawRoundRect(
        color = Color(0xFFC77800).copy(alpha = 0.8f),
        topLeft = Offset(-grooveHalf, -grooveHalf),
        size = Size(2f * grooveHalf, 2f * grooveHalf),
        cornerRadius = CornerRadius(cornerFor(grooveHalf))
    )

    // 5) The face: light yellow upper left, deeper gold lower right.
    val faceHalf = 0.80f * r
    val faceCorner = cornerFor(faceHalf)
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFE135), Color(0xFFFFB300)),
            center = Offset(-0.25f * r, -0.35f * r),
            radius = 1.3f * r
        ),
        topLeft = Offset(-faceHalf, -faceHalf),
        size = Size(2f * faceHalf, 2f * faceHalf),
        cornerRadius = CornerRadius(faceCorner)
    )

    // Thin orange edge around the face for depth.
    val edge = 0.04f * r
    drawRoundRect(
        color = Color(0xFFFF8F00).copy(alpha = 0.5f),
        topLeft = Offset(-faceHalf + edge / 2f, -faceHalf + edge / 2f),
        size = Size(2f * faceHalf - edge, 2f * faceHalf - edge),
        cornerRadius = CornerRadius((faceCorner - edge / 2f).coerceAtLeast(0f)),
        style = Stroke(width = edge)
    )

    // 6) Shiny highlight on the upper half of the face (clipped to the face shape).
    val facePath = Path().apply {
        addRoundRect(
            RoundRect(
                left = -faceHalf, top = -faceHalf,
                right = faceHalf, bottom = faceHalf,
                cornerRadius = CornerRadius(faceCorner)
            )
        )
    }
    clipPath(facePath) {
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.05f)),
                start = Offset(0f, -0.8f * r),
                end = Offset(0f, -0.05f * r)
            ),
            topLeft = Offset(-0.72f * r, -0.78f * r),
            size = Size(1.44f * r, 0.72f * r),
            cornerRadius = CornerRadius(0.3f * r)
        )
    }

    // Short bright line on the ring, upper-left corner, like light on a rounded edge.
    val h = 0.92f * r
    val c = (outerCorner - (r - h)).coerceAtLeast(0f)
    val glint = Path().apply {
        moveTo(-h, -h + c + 0.25f * r)
        lineTo(-h, -h + c)
        arcTo(Rect(-h, -h, -h + 2f * c, -h + 2f * c), 180f, 90f, false)
        lineTo(-h + c + 0.25f * r, -h)
    }
    drawPath(
        path = glint,
        color = Color.White.copy(alpha = 0.6f),
        style = Stroke(width = 0.06f * r, cap = StrokeCap.Round)
    )
}




