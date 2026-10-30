package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// FOR FUTURE USE NOT NOW DO NOT TOUCH
@Composable
internal fun BoxScope.HandInstructionCard(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFFFF3E0), RoundedCornerShape(28.dp))
                .border(6.dp, Color(0xFFFFB300), RoundedCornerShape(28.dp))
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "\u270B Hand mode",
                color = Color(0xFFEF6C00),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Move your hand over the falling fruits to cut them.",
                color = Color(0xFF5D4037),
                fontSize = 22.sp
            )
            Text(
                text = "You can also swipe the screen with your finger.",
                color = Color(0xFF5D4037),
                fontSize = 22.sp
            )
            Text(
                text = "\uD83D\uDCA3 Avoid the bombs!",
                color = Color(0xFF5D4037),
                fontSize = 22.sp
            )
            Text(
                text = "Tap anywhere to start",
                color = Color(0xFF2E7D32),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
