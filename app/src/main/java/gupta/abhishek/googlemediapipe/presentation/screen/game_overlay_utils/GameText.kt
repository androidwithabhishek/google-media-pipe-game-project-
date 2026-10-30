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


@Composable
internal fun GlossyText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier,
    shadow: Shadow = Shadow(
        color = Color.Black.copy(alpha = 0.75f),
        offset = Offset(3f, 4f),
        blurRadius = 6f
    )
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
            shadow = shadow,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    )
}


@Composable
internal fun GameOverText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    tiltDegrees: Float,
    modifier: Modifier = Modifier
) {
    GlossyText(
        text = text,
        fontSize = fontSize,
        color = color,
        modifier = modifier.graphicsLayer { rotationZ = tiltDegrees },
        shadow = Shadow(
            color = Color.Black.copy(alpha = GAME_OVER_TEXT_SHADOW_ALPHA),
            offset = Offset(GAME_OVER_TEXT_SHADOW_OFFSET_PX, GAME_OVER_TEXT_SHADOW_OFFSET_PX),
            blurRadius = GAME_OVER_TEXT_SHADOW_BLUR_PX
        )
    )
}