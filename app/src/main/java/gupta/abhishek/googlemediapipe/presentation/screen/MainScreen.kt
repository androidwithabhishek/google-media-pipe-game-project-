package gupta.abhishek.googlemediapipe.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandOverlay
import gupta.abhishek.googlemediapipe.core.sound.GameSounds
import gupta.abhishek.googlemediapipe.presentation.MainViewModel
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GameScreen
import gupta.abhishek.googlemediapipe.presentation.screen.game_overlay_utils.CameraPermissionAlert

// ---------------------------------------------------------------------
// MainScreen
//   The real app screen. Decides when the camera is really on:
//     camera on  = the player wants it AND the permission is granted
//     otherwise  = touch mode, and the camera is never opened
// ---------------------------------------------------------------------
@Composable
fun MainScreen(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    showHandAnimation: Boolean = true,
    onCameraNeeded: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel(),
) {
    val bestScore by viewModel.bestScore.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    // Camera permission right now. Checked again when the app comes back (e.g. from Settings).
    var cameraPermissionGranted by remember { mutableStateOf(context.hasCameraPermission()) }

    // Shows the "camera permission required" alert.
    var showPermissionAlert by remember { mutableStateOf(false) }

    // True when Android will not show the permission dialog again ("Don't ask again").
    var permissionDeniedPermanently by remember { mutableStateOf(false) }

    // Camera mode is really on only when the player wants it AND the permission is granted.
    val cameraOn = settings.handControlEnabled && cameraPermissionGranted

    // Asks Android for the camera permission (the system dialog).
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted = granted
        if (granted) {
            permissionDeniedPermanently = false
            showPermissionAlert = false
            viewModel.setHandControlEnabled(true)
        } else {
            // If Android will not ask again, the alert offers the app settings instead.
            permissionDeniedPermanently =
                activity != null && !activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        }
    }

    // The camera button.
    val onToggleCamera: () -> Unit = {

        when {
            cameraOn -> {
                viewModel.setHandControlEnabled(false)            // turning off: no permission needed
            }
            cameraPermissionGranted -> {
                viewModel.setHandControlEnabled(true)             // turning on: permission already there
            }
            else -> {
                showPermissionAlert = true                        // turning on without permission
            }
        }
    }

    // Sound effects and music. Music pauses in the background and resumes when the app returns.
    val sounds = remember { GameSounds(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        sounds.startMusic()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    sounds.startMusic()
                    // The player may have changed the permission in Settings.
                    cameraPermissionGranted = context.hasCameraPermission()
                    if (cameraPermissionGranted) showPermissionAlert = false
                }
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



    // Load the hand model only when the camera is really in use (in the background).
    LaunchedEffect(cameraOn) {
        if (cameraOn) onCameraNeeded()
    }

    // Sound effects and music stop while the permission alert is open.
    LaunchedEffect(showPermissionAlert) {
        sounds.setAlertPaused(showPermissionAlert)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GameScreen(
            helper = helper,
            handResult = handResult,
            modifier = Modifier.fillMaxSize(),
            bestScoreFromDatabase = bestScore,
            onGameOver = { finalScore -> viewModel.saveScore(finalScore) },
            handControlEnabled = cameraOn,
            showHandInstruction = !settings.handInstructionSeen,
            onToggleHandControl = onToggleCamera,
            onHandInstructionSeen = { viewModel.markHandInstructionSeen() },
            sounds = sounds,
            forcePause = showPermissionAlert
        )

        // The hand overlay only shows while the camera is really on.
        if (showHandAnimation && cameraOn) {
            HandOverlay(
                result = handResult,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Permission alert, drawn on top of everything.
        if (showPermissionAlert) {
            CameraPermissionAlert(
                permanentlyDenied = permissionDeniedPermanently,
                onPrimaryClick = {
                    if (permissionDeniedPermanently) {
                        context.openAppSettings()
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onCancel = { showPermissionAlert = false }
            )
        }
    }


}

// True when the app has the camera permission right now.
private fun Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

// Opens this app's page in the phone settings, where the player can allow the camera.
private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}