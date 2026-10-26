package gupta.abhishek.googlemediapipe.presentation.screen.game_overlay_utils



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import gupta.abhishek.googlemediapipe.core.fonts.GameFonts

// ---------------------------------------------------------------------
// GlossyText
//   Big bold candy-style text with a dark shadow. Used for all game text.
// ---------------------------------------------------------------------
@Composable
internal fun GlossyText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = GameFonts.JungleRoarFont,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = fontSize,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.75f),
                offset = Offset(3f, 4f),
                blurRadius = 6f
            ),
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    )
}

// ---------------------------------------------------------------------
// GameOverText
//   GlossyText with a tilt angle. Used for the game over card lines.
// ---------------------------------------------------------------------
@Composable
internal fun GameOverText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    tiltDegrees: Float
) {
    GlossyText(
        text = text,
        fontSize = fontSize,
        color = color,
        modifier = Modifier.graphicsLayer { rotationZ = tiltDegrees }
    )
}