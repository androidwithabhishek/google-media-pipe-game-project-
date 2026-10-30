package gupta.abhishek.googlemediapipe.presentation.game_overlay_utils.hud_utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gupta.abhishek.googlemediapipe.core.fonts.GlossyText

// ---------------------------------------------------------------------
// CameraPermissionAlert
//   Shown when the player turns camera mode on but the camera permission is missing.
//   permanentlyDenied = true  -> the button opens the app settings
//   permanentlyDenied = false -> the button asks for the permission
//   Cancel (or tapping outside the card) closes it and the game stays in touch mode.
// ---------------------------------------------------------------------
@Composable
fun CameraPermissionAlert(
    permanentlyDenied: Boolean,
    onPrimaryClick: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCancel
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFFFF3E0), RoundedCornerShape(28.dp))
                .border(6.dp, Color(0xFFFFB300), RoundedCornerShape(28.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}   // taps on the card itself do not close the alert
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            GlossyText(
                text = "Camera permission required",
                fontSize = 28.sp,
                color = Color(0xFFEF6C00)
            )

            Text(
                text = if (permanentlyDenied) {
                    "The camera is turned off for this app. Open the settings, allow the camera, " +
                            "then come back to play with your hand. You can still play with touch."
                } else {
                    "Hand mode needs the camera to see your hand. Allow the camera to play with your hand. " +
                            "You can still play with touch."
                },
                color = Color(0xFF5D4037),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {


                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9E9E9E),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Cancel", fontSize = 18.sp)
                }


                Button(
                    onClick = onPrimaryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (permanentlyDenied) "Open settings" else "Ask permission",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }


            }
        }
    }
}