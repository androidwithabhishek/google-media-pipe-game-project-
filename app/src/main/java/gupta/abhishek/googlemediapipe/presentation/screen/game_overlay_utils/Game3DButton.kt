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


@Composable
internal fun Game3DButton(
    sizeDp: Dp,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onClick: () -> Unit,
    icon: DrawScope.(radius: Float) -> Unit
) {
    Canvas(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        // The button is a bit smaller than the box, so its shadow still fits inside.
        val r = size.minDimension / 2f * 0.88f

        translate(size.width / 2f, size.height / 2f) {

            // The yellow 3D button, then the icon on it.
            drawYellowRoundButton(r)
            icon(r)

            // Green ring that fills up clockwise while a hand holds on the button.
            if (progress > 0f) {
                drawArc(
                    color = Color(0xFF4CAF50),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(-r * 0.94f, -r * 0.94f),
                    size = Size(r * 1.88f, r * 1.88f),
                    style = Stroke(width = r * 0.14f, cap = StrokeCap.Round)
                )
            }
        }
    }
}



