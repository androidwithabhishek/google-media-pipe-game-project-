package gupta.abhishek.googlemediapipe.presentation.screen.game_overlay_utils


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import gupta.abhishek.googlemediapipe.core.game_assets.drawYellowRoundButton
import gupta.abhishek.googlemediapipe.core.game_assets.drawYellowSquareButton
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
private fun roundedSquareProgressPath(half: Float, corner: Float): Path = Path().apply {
    val h = half
    val c = corner
    moveTo(0f, -h)
    lineTo(h - c, -h)
    arcTo(Rect(h - 2f * c, -h, h, -h + 2f * c), -90f, 90f, false)
    lineTo(h, h - c)
    arcTo(Rect(h - 2f * c, h - 2f * c, h, h), 0f, 90f, false)
    lineTo(-h + c, h)
    arcTo(Rect(-h, h - 2f * c, -h + 2f * c, h), 90f, 90f, false)
    lineTo(-h, -h + c)
    arcTo(Rect(-h, -h, -h + 2f * c, -h + 2f * c), 180f, 90f, false)
    close()
}



@Composable
internal fun Game3DSquareButton(
    sizeDp: Dp,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onClick: () -> Unit,
    icon: DrawScope.(radius: Float) -> Unit
) {
    Canvas(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(24)) // percent; keeps the click area square-ish
            .clickable(onClick = onClick)
    ) {
        // Slightly smaller than the box so the shadow still fits inside.
        val r = size.minDimension / 2f * 0.88f

        translate(size.width / 2f, size.height / 2f) {

            drawYellowSquareButton(r)
            icon(r)

            // Green ring that fills clockwise along the square's edge.
            if (progress > 0f) {
                val half = r * 0.94f
                val corner = (0.38f * r - (r - half)).coerceAtLeast(0f)
                val ringPath = roundedSquareProgressPath(half, corner)

                val measure = PathMeasure()
                measure.setPath(ringPath, false)
                val segment = Path()
                measure.getSegment(
                    0f,
                    measure.length * progress.coerceIn(0f, 1f),
                    segment,
                    true
                )

                drawPath(
                    path = segment,
                    color = Color(0xFF4CAF50),
                    style = Stroke(width = r * 0.14f, cap = StrokeCap.Round)
                )
            }
        }
    }
}