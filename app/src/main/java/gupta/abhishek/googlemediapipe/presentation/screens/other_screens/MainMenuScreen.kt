package gupta.abhishek.googlemediapipe.presentation.screens.other_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import gupta.abhishek.googlemediapipe.R
import gupta.abhishek.googlemediapipe.core.fonts.GameFonts
import gupta.abhishek.googlemediapipe.core.fonts.GlossyText
import gupta.abhishek.googlemediapipe.core.sound.GameSounds


// ---------------------------------------------------------------------
// MainMenuScreen
//   The first screen. Play starts a new game, or Continue returns to the game
//   that is still running. Also: Fruit test, sound on/off, music on/off, high score.
// ---------------------------------------------------------------------
@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    onContinueClick: () -> Unit,
    onFruitTestClick: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
    onMenuClick: () -> Unit = {},
) {
    val bestScore by viewModel.bestScore.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val gameInProgress by viewModel.gameInProgress.collectAsState()

    val context = LocalContext.current
    val sounds = remember { GameSounds(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Sound and music follow the saved settings, also when they change.
    LaunchedEffect(settings.soundEnabled) { sounds.setSoundEnabled(settings.soundEnabled) }
    LaunchedEffect(settings.musicEnabled) { sounds.setMusicEnabled(settings.musicEnabled) }

    // Menu music: starts here, pauses in the background, stops when the menu closes.
    DisposableEffect(lifecycleOwner) {
        sounds.setSoundEnabled(settings.soundEnabled)
        sounds.setMusicEnabled(settings.musicEnabled)
        sounds.startMusic()

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sounds.startMusic()
                Lifecycle.Event.ON_PAUSE -> sounds.pauseMusic()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sounds.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background picture.
        Image(
            painter = painterResource(id = R.drawable.gamebg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Title, high score and the main buttons, in the middle.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GlossyText(
                text = "FRUIT GAME",
                fontSize = 60.sp,
                color = Color(0xFFFFD54F)
            )

            GlossyText(
                text = "High score: $bestScore",
                fontSize = 34.sp,
                color = Color(0xFF69F0AE),
                modifier = Modifier.padding(top = 8.dp, bottom = 36.dp)
            )

            MenuButton(
                text = if (gameInProgress) "Continue" else "Play",
                color = Color(0xFF4CAF50),
                onClick = {
                    sounds.playUiTouch()
                    if (gameInProgress) onContinueClick() else onPlayClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Fruit test",
                color = Color(0xFF1E88E5),
                onClick = {
                    sounds.playUiTouch()
                    onFruitTestClick()
                }
            )
        }

        // Sound and music on/off, bottom right.
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ToggleIconButton(
                on = settings.soundEnabled,
                onIcon = Icons.Filled.VolumeUp,
                offIcon = Icons.Filled.VolumeOff,
                description = "Sound",
                onClick = { viewModel.toggleSound() }
            )

            ToggleIconButton(
                on = settings.soundEnabled && settings.musicEnabled,
                onIcon = Icons.Filled.MusicNote,
                offIcon = Icons.Filled.MusicOff,
                description = "Music",
                onClick = { viewModel.toggleMusic() }
            )
        }
    }
}

// A big rounded button with the game font.
@Composable
private fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(300.dp)
            .height(72.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontFamily = GameFonts.JungleRoarFont,
            fontWeight = FontWeight.Bold
        )
    }
}

// A round dark button that shows one icon when on and another when off.
@Composable
private fun ToggleIconButton(
    on: Boolean,
    onIcon: ImageVector,
    offIcon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Icon(
            imageVector = if (on) onIcon else offIcon,
            contentDescription = if (on) "$description on" else "$description off",
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(36.dp)
        )
    }
}