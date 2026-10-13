package gupta.abhishek.googlemediapipe.presentation

import android.content.Context
import android.hardware.display.DisplayManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.fruits.Blast
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import gupta.abhishek.googlemediapipe.core.fruits.Fruits
import gupta.abhishek.googlemediapipe.core.fruits.drawBlast
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandAnalyzer
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import kotlin.random.Random


// ---------------------------------------------------------------------
// GAME SETTINGS
// Change these numbers to tune the game.
// ---------------------------------------------------------------------

// ----- Fruit size -----
private const val FRUIT_BASE_RADIUS_PX = 120f     // base size of every fruit, in pixels
private const val FRUIT_SIZE_MULTIPLIER = 1.2f    // global size: 1.2f = 20% bigger, 0.8f = 20% smaller

// ----- Bombs -----
private const val BOMB_SPAWN_CHANCE = 0.18f       // chance the next item is a bomb (0.18 = 18%)
private const val BOMBS_UNLOCK_SCORE = 3          // no bombs until the score reaches this

// ----- Camera shake on cuts -----
private const val CUT_SHAKE_STRENGTH_PX = 10f     // how far the picture jolts on every cut
private const val CUT_SHAKE_DURATION_SECONDS = 0.15f

// ----- Movement -----
private const val FRUIT_SPIN_DEGREES_PER_SECOND = 90f
private const val START_FALL_SPEED_PX_PER_SECOND = 400f
private const val FALL_SPEED_INCREASE_PER_POINT = 40f
private const val MAX_FALL_SPEED_PX_PER_SECOND = 1600f

// ----- Hand catching -----
private const val CATCH_EXTRA_REACH_PX = 20f      // extra distance so catching feels easier

// ----- Lives -----
private const val STARTING_LIVES = 5

// ----- Restart button (round, top right, shown on game over). Sizes in dp. -----
private const val RESTART_BUTTON_SIZE_DP = 120f
private const val RESTART_BUTTON_MARGIN_TOP_DP = 50f
private const val RESTART_BUTTON_MARGIN_END_DP = 50f
private const val RESTART_HOLD_SECONDS = 0.5f     // keep your hand on the button this long (0 = instant)


// ---------------------------------------------------------------------
// fruitRadiusFor
//   The real on-screen radius of a fruit, in pixels.
//   Combines the base size, the fruit's own sizeScale, and the global multiplier.
//   Use this everywhere a fruit is drawn, cut, or hit-checked.
// ---------------------------------------------------------------------
private fun fruitRadiusFor(fruit: FruitType): Float =
    FRUIT_BASE_RADIUS_PX * fruit.sizeScale * FRUIT_SIZE_MULTIPLIER


// ---------------------------------------------------------------------
// CameraPreview
//   Shows the camera, drops fruits and bombs on top of it,
//   and scores a point when a hand touches a falling fruit.
//
//   helper     -> MediaPipe helper that receives camera frames
//   handResult -> latest hand detection result from MainActivity
// ---------------------------------------------------------------------
@Composable
fun CameraPreview(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    modifier: Modifier = Modifier
) {

    // ---------------- GAME STATE ----------------

    // Current score. Saved across screen rotation.
    var currentScore by rememberSaveable { mutableStateOf(0) }

    // Best score so far. Saved across screen rotation.
    var bestScore by rememberSaveable { mutableStateOf(0) }

    // Lives left. Game over when this reaches 0.
    var livesLeft by rememberSaveable { mutableStateOf(STARTING_LIVES) }

    // Whether the "new best score" message was already shown this game.
    var newBestAnnounced by remember { mutableStateOf(false) }

    // Size of the whole screen area in pixels (needed to know where the bottom is).
    var screenSize by remember { mutableStateOf(IntSize.Zero) }


    // ---------------- FALLING ITEM STATE ----------------

    // Center of the falling item.
    var itemX by remember { mutableStateOf(0f) }
    var itemY by remember { mutableStateOf(-FRUIT_BASE_RADIUS_PX) }

    // The fruit falling right now.
    var currentFruit by remember { mutableStateOf(Fruits.all.random()) }

    // Spin angle of the falling fruit, in degrees.
    var fruitSpinAngle by remember { mutableStateOf(0f) }

    // The bomb falling right now. null means the falling item is a fruit.
    var currentBomb by remember { mutableStateOf<BombType?>(null) }


    // ---------------- EFFECTS STATE ----------------

    // Cut animations currently running (knife, halves, juice).
    val runningCuts = remember { mutableStateListOf<FruitCut>() }

    // Explosions currently running.
    val runningBlasts = remember { mutableStateListOf<Blast>() }

    // Camera shake: current offset, plus the running shake's strength and timing.
    var shakeOffsetX by remember { mutableStateOf(0f) }
    var shakeOffsetY by remember { mutableStateOf(0f) }
    var shakeStrength by remember { mutableStateOf(0f) }
    var shakeSecondsLeft by remember { mutableStateOf(0f) }
    var shakeSecondsTotal by remember { mutableStateOf(0.001f) }

    // How long the hand has been on the restart button, 0.0 to 1.0 of the hold time.
    var restartHoldProgress by remember { mutableStateOf(0f) }


    // Convert the restart button settings from dp to pixels (hand points are in pixels).
    val density = LocalDensity.current
    val restartButtonSizePx = with(density) { RESTART_BUTTON_SIZE_DP.dp.toPx() }
    val restartButtonTopPx = with(density) { RESTART_BUTTON_MARGIN_TOP_DP.dp.toPx() }
    val restartButtonEndPx = with(density) { RESTART_BUTTON_MARGIN_END_DP.dp.toPx() }


    // ---------------- HELPER FUNCTIONS ----------------

    // Drops the next item from the top: usually a fruit, sometimes a bomb.
    val dropNextItem = {
        if (currentScore >= BOMBS_UNLOCK_SCORE && Random.nextFloat() < BOMB_SPAWN_CHANCE) {
            currentBomb = Bombs.random()
        } else {
            currentBomb = null
            currentFruit = Fruits.all.random()
        }
        itemX = randomItemX(screenSize.width)
        itemY = -FRUIT_BASE_RADIUS_PX
    }

    // Starts a camera shake. A weaker shake never replaces a stronger one still running.
    val startCameraShake = { strength: Float, durationSeconds: Float ->
        val currentStrengthLeft = shakeStrength * (shakeSecondsLeft / shakeSecondsTotal).coerceIn(0f, 1f)
        if (strength >= currentStrengthLeft) {
            shakeStrength = strength
            shakeSecondsTotal = durationSeconds
            shakeSecondsLeft = durationSeconds
        }
    }

    // Resets the whole game to the start.
    val restartGame = {
        currentScore = 0
        livesLeft = STARTING_LIVES
        newBestAnnounced = false
        runningCuts.clear()
        runningBlasts.clear()
        shakeSecondsLeft = 0f
        dropNextItem()
    }

    // The game loop runs for a long time, so it must always read the newest hand result.
    val latestHandResult by rememberUpdatedState(handResult)


    // ---------------- GAME LOOP ----------------
    // Runs once the screen size is known, and again if the size changes.
    LaunchedEffect(screenSize) {

        if (screenSize.width == 0 || screenSize.height == 0) return@LaunchedEffect

        dropNextItem()

        var previousFrameTimeNanos = 0L

        while (true) {

            withFrameNanos { frameTimeNanos ->

                // Seconds since the last frame.
                if (previousFrameTimeNanos == 0L) previousFrameTimeNanos = frameTimeNanos
                val secondsSinceLastFrame = (frameTimeNanos - previousFrameTimeNanos) / 1_000_000_000f
                previousFrameTimeNanos = frameTimeNanos

                // ----- Restart button hold check (only on game over) -----
                val restartButtonRadiusPx = restartButtonSizePx / 2f
                val restartButtonCenterX = screenSize.width - restartButtonEndPx - restartButtonRadiusPx
                val restartButtonCenterY = restartButtonTopPx + restartButtonRadiusPx

                val handIsOnRestartButton = livesLeft <= 0 && isHandInCircle(
                    result = latestHandResult,
                    centerX = restartButtonCenterX,
                    centerY = restartButtonCenterY,
                    radius = restartButtonRadiusPx,
                    width = screenSize.width,
                    height = screenSize.height
                )

                if (handIsOnRestartButton) {
                    restartHoldProgress += secondsSinceLastFrame
                    if (restartHoldProgress >= RESTART_HOLD_SECONDS) {
                        restartHoldProgress = 0f
                        restartGame()
                    }
                } else {
                    restartHoldProgress = 0f
                }

                // ----- Move cut animations and explosions forward, remove finished ones -----
                for (cut in runningCuts) {
                    cut.age += secondsSinceLastFrame
                }
                runningCuts.removeAll { it.age > CUT_DURATION }

                for (blast in runningBlasts) {
                    blast.age += secondsSinceLastFrame
                }
                runningBlasts.removeAll { it.age > it.duration }

                // ----- Camera shake -----
                if (shakeSecondsLeft > 0f) {
                    shakeSecondsLeft -= secondsSinceLastFrame
                    val strengthNow = shakeStrength * (shakeSecondsLeft / shakeSecondsTotal).coerceIn(0f, 1f)
                    shakeOffsetX = (Random.nextFloat() * 2f - 1f) * strengthNow
                    shakeOffsetY = (Random.nextFloat() * 2f - 1f) * strengthNow
                } else {
                    shakeOffsetX = 0f
                    shakeOffsetY = 0f
                }

                // ----- Game over: freeze the item and wait for restart -----
                if (livesLeft <= 0) return@withFrameNanos

                // ----- Falling speed: grows with the score, up to a limit -----
                val fallSpeed = minOf(
                    START_FALL_SPEED_PX_PER_SECOND + currentScore * FALL_SPEED_INCREASE_PER_POINT,
                    MAX_FALL_SPEED_PX_PER_SECOND
                )

                // 1) Move the item down and spin the fruit.
                itemY += fallSpeed * secondsSinceLastFrame
                fruitSpinAngle += FRUIT_SPIN_DEGREES_PER_SECOND * secondsSinceLastFrame

                // 2) Check the hit. Bombs use their own size. Fruits use the shared size rule.
                val bomb = currentBomb
                val itemRadius = if (bomb != null) {
                    FRUIT_BASE_RADIUS_PX * bomb.sizeScale
                } else {
                    fruitRadiusFor(currentFruit)
                }

                val itemWasTouched = isItemTouchedByHand(
                    result = latestHandResult,
                    itemX = itemX,
                    itemY = itemY,
                    radius = itemRadius,
                    width = screenSize.width,
                    height = screenSize.height
                )

                if (itemWasTouched && bomb != null) {
                    // ---------- A BOMB WAS CUT ----------
                    runningBlasts.add(Blast(bomb, itemX, itemY, itemRadius))
                    startCameraShake(bomb.shakePower, bomb.shakeSeconds)

                    livesLeft = (livesLeft - bomb.livesLost).coerceAtLeast(0)

                    if (livesLeft <= 0) {
                        itemY = -itemRadius * 2f   // hide the bomb, game over
                    } else {
                        dropNextItem()
                    }

                } else if (itemWasTouched) {
                    // ---------- A FRUIT WAS CUT ----------
                    currentScore += 1

                    runningCuts.add(
                        FruitCut(currentFruit, itemX, itemY, fruitRadiusFor(currentFruit), fruitSpinAngle)
                    )
                    startCameraShake(CUT_SHAKE_STRENGTH_PX, CUT_SHAKE_DURATION_SECONDS)

                    val beatBestScoreNow = currentScore > bestScore && bestScore > 0 && !newBestAnnounced
                    if (beatBestScoreNow) {
                        newBestAnnounced = true
                    }

                    if (currentScore > bestScore) {
                        bestScore = currentScore
                    }

                    dropNextItem()

                } else if (itemY - itemRadius > screenSize.height) {
                    // ---------- THE ITEM FELL OFF THE BOTTOM ----------
                    if (bomb != null) {
                        // Missing a bomb is good: just drop the next item.
                        dropNextItem()
                    } else {
                        // Missing a fruit costs one life.
                        livesLeft = livesLeft - 1

                        if (livesLeft <= 0) {
                            itemY = -itemRadius * 2f   // hide the fruit, game over
                        } else {
                            dropNextItem()
                        }
                    }
                }
            }
        }
    }


    // ---------------- UI ----------------
    Box(
        modifier = modifier.onSizeChanged { newSize ->
            screenSize = newSize
        }
    ) {

        // Camera picture and the game drawings share one box so they shake together.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = shakeOffsetX
                    translationY = shakeOffsetY
                }
        ) {

            // Camera picture.
            CameraView(helper = helper)

            // Falling item, cut animations and explosions.
            Canvas(modifier = Modifier.fillMaxSize()) {

                val bomb = currentBomb

                if (bomb != null) {
                    // Bombs do not spin, so the fuse stays on top.
                    translate(itemX, itemY) {
                        drawBomb(bomb, FRUIT_BASE_RADIUS_PX * bomb.sizeScale, fruitSpinAngle)
                    }
                } else {
                    translate(itemX, itemY) {
                        rotate(degrees = fruitSpinAngle, pivot = Offset.Zero) {
                            currentFruit.draw(this, fruitRadiusFor(currentFruit))
                        }
                    }
                }

                for (cut in runningCuts) {
                    drawFruitCut(cut)
                }

                for (blast in runningBlasts) {
                    drawBlast(blast)
                }
            }
        }

        // Info panel at the top center: Score | Lives | Best | Hand status.
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(
                    Color.Black.copy(alpha = 0.65f),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Score: $currentScore",
                color = Color.White,
                fontSize = 22.sp
            )

            val livesColor = if (livesLeft >= 3) Color(0xFF4CAF50) else Color(0xFFFF5252)
            val livesText = if (livesLeft >= 3) "Lives: $livesLeft" else "\u26A0\uFE0F Lives: $livesLeft"
            Text(
                text = livesText,
                color = livesColor,
                fontSize = 22.sp
            )

            val isAtBestScore = currentScore > 0 && currentScore >= bestScore
            val bestEmoji = if (isAtBestScore) "\uD83D\uDE0E" else "\uD83C\uDFC6"
            Text(
                text = "$bestEmoji Best: $bestScore",
                color = Color.Yellow,
                fontSize = 22.sp
            )

            val handIsVisible = handResult?.landmarks()?.isEmpty() == false
            Text(
                text = if (handIsVisible) "\u270B Hand OK" else "\u270B Show your hand",
                color = if (handIsVisible) Color(0xFF4CAF50) else Color(0xFFFF5252),
                fontSize = 22.sp
            )
        }

        // Round restart button, top right. Only on game over.
        // Works by touch and by holding your hand on it.
        if (livesLeft <= 0) {
            val holdFraction = if (RESTART_HOLD_SECONDS > 0f)
                (restartHoldProgress / RESTART_HOLD_SECONDS).coerceIn(0f, 1f) else 0f
            Button(
                onClick = { restartGame() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = RESTART_BUTTON_MARGIN_TOP_DP.dp, end = RESTART_BUTTON_MARGIN_END_DP.dp)
                    .size(RESTART_BUTTON_SIZE_DP.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lerp(
                        Color.Black.copy(alpha = 0.65f),
                        Color(0xFF4CAF50),
                        holdFraction
                    ),
                    contentColor = Color.White
                )
            ) {
                Text("\uD83D\uDD04", fontSize = 70.sp)
            }
        }

        // Game over card.
        if (livesLeft <= 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        Color(0xFF1B1B2F).copy(alpha = 0.92f),
                        RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFF5252),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 48.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "\uD83D\uDC80 GAME OVER",
                    color = Color(0xFFFF5252),
                    fontSize = 36.sp
                )

                Text(
                    text = "Score: $currentScore",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                val isNewBest = currentScore > 0 && currentScore >= bestScore
                Text(
                    text = if (isNewBest) "\uD83D\uDE0E New high score!"
                    else "\uD83C\uDFC6 Best: $bestScore",
                    color = Color.Yellow,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Button(
                    onClick = { restartGame() },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Play again", fontSize = 22.sp)
                }
            }
        }
    }
}


// ---------------------------------------------------------------------
// CameraView
//   Camera setup. Also tells the frame grabber when the phone rotates,
//   so MediaPipe always gets an upright picture.
// ---------------------------------------------------------------------
@Composable
private fun CameraView(helper: HandLandmarkerHelper) {

    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }

    // Watches screen rotation and passes it to the frame grabber.
    DisposableEffect(Unit) {

        val displayManager =
            context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val displayListener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {}
            override fun onDisplayRemoved(displayId: Int) {}
            override fun onDisplayChanged(displayId: Int) {
                val display = previewView.display
                if (display != null) {
                    imageAnalyzer.targetRotation = display.rotation
                }
            }
        }

        displayManager.registerDisplayListener(displayListener, null)

        onDispose { displayManager.unregisterDisplayListener(displayListener) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                preview.surfaceProvider = previewView.surfaceProvider

                previewView.display?.let { imageAnalyzer.targetRotation = it.rotation }

                imageAnalyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    HandAnalyzer(helper)
                )

                // Front camera. Use DEFAULT_BACK_CAMERA for the back camera.
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    ctx as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}


// ---------------------------------------------------------------------
// randomItemX
//   Random horizontal spot for a new item, keeping it fully on screen.
// ---------------------------------------------------------------------
private fun randomItemX(screenWidthPx: Int): Float {
    val minX = FRUIT_BASE_RADIUS_PX * 1.4f
    val maxX = screenWidthPx - FRUIT_BASE_RADIUS_PX * 1.4f
    return Random.nextFloat() * (maxX - minX) + minX
}


// ---------------------------------------------------------------------
// isItemTouchedByHand
//   True if ANY of the 21 points of ANY hand is inside (or near) the item.
//   MediaPipe gives 0.0 to 1.0 fractions, so we multiply by the screen size.
//   X is flipped (1 - x) because the front camera is mirrored.
// ---------------------------------------------------------------------
private fun isItemTouchedByHand(
    result: HandLandmarkerResult?,
    itemX: Float,
    itemY: Float,
    radius: Float,
    width: Int,
    height: Int
): Boolean {

    if (result == null) return false

    val reachPx = radius + CATCH_EXTRA_REACH_PX

    for (hand in result.landmarks()) {
        for (point in hand) {

            val pointXPx = (1f - point.x()) * width
            val pointYPx = point.y() * height

            val dx = pointXPx - itemX
            val dy = pointYPx - itemY
            val distanceSquared = dx * dx + dy * dy

            if (distanceSquared <= reachPx * reachPx) {
                return true
            }
        }
    }

    return false
}


// ---------------------------------------------------------------------
// isHandInCircle
//   True if ANY point of ANY hand is inside a circle on screen.
//   Same pixel and mirror math as isItemTouchedByHand. Used by the restart button.
// ---------------------------------------------------------------------
private fun isHandInCircle(
    result: HandLandmarkerResult?,
    centerX: Float,
    centerY: Float,
    radius: Float,
    width: Int,
    height: Int
): Boolean {

    if (result == null) return false

    for (hand in result.landmarks()) {
        for (point in hand) {

            val pointXPx = (1f - point.x()) * width
            val pointYPx = point.y() * height

            val dx = pointXPx - centerX
            val dy = pointYPx - centerY

            if (dx * dx + dy * dy <= radius * radius) {
                return true
            }
        }
    }

    return false
}