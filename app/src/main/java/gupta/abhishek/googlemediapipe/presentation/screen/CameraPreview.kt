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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.fruits.Fruits
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandAnalyzer
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import kotlin.random.Random


// ---------------------------------------------------------------------
// GAME SETTINGS (change these numbers to tune the game)
// ---------------------------------------------------------------------
private const val BALL_RADIUS = 120f      // size of the falling fruit, in pixels
private const val BOMB_CHANCE = 0.18f    // chance that the next item is a bomb (0.18 = 18%)
private const val BOMBS_START_SCORE = 3  // no bombs until you have this many points
private const val CUT_SHAKE_POWER = 10f  // small camera shake on every fruit cut, in pixels
private const val CUT_SHAKE_SECONDS = 0.15f
private const val FRUIT_SPIN = 90f       // how fast the falling fruit turns, degrees per second
private const val CATCH_EXTRA = 20f      // extra forgiving distance so catching feels easier
private const val START_FALL_SPEED = 400f   // falling speed at score 0, pixels per second
private const val SPEED_PER_POINT = 40f     // extra speed added for every point scored
private const val MAX_FALL_SPEED = 1600f    // speed limit so the game stays playable
private const val START_LIVES = 5           // lives you get at the start

// RESTART BUTTON (round, top right, only visible on game over). Sizes are in dp.
private const val RESTART_SIZE_DP = 96f         // diameter of the round button
private const val RESTART_MARGIN_TOP_DP = 24f   // distance from the top edge
private const val RESTART_MARGIN_END_DP = 24f   // distance from the right edge
private const val HOLD_TO_RESTART_SECONDS = 0.5f // keep your hand on the button this long (0 = instant)

// ---------------------------------------------------------------------
// CameraPreview
//   Shows the camera, draws falling fruits and bombs on top of it,
//   and adds +1 to the score when any hand landmark touches the circle.
//
//   helper     -> MediaPipe helper (same as before, feeds camera frames to MediaPipe)
//   handResult -> latest hand detection result coming from MainActivity (NEW)
// ---------------------------------------------------------------------
@Composable
fun CameraPreview(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    modifier: Modifier = Modifier
) {

    // ---------------- VARIABLES ----------------

    // THE SCORE. rememberSaveable = survives screen rotation / activity recreate.
    var score by rememberSaveable { mutableStateOf(0) }

    // HIGH SCORE. Best score so far. Also saved across screen rotation.
    var highScore by rememberSaveable { mutableStateOf(0) }

    // LIVES. Starts at 5, goes down by 1 every time a ball is missed.
    // When it reaches 0 the game is over.
    var lives by rememberSaveable { mutableStateOf(START_LIVES) }

    // SOUNDS. Created once, holds all the sound effects and the music (see GameSounds.kt).
    val context = LocalContext.current
    val sounds = remember { GameSounds(context) }

    // Did we already play the "new high score" fanfare in this game? (so it plays only once)
    var recordAnnounced by remember { mutableStateOf(false) }

    // Newest lives value for the lifecycle observer below (same trick as latestHand).
    val latestLives by rememberUpdatedState(lives)

    // Size of this whole screen area in pixels (we need it to know where "bottom" is).
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    // Ball position (center of the circle). Starts just above the top of the screen.
    var ballX by remember { mutableStateOf(0f) }
    var ballY by remember { mutableStateOf(-BALL_RADIUS) }

    // The fruit that is falling right now (picked from the Fruits list in Fruits.kt).
    var currentFruit by remember { mutableStateOf(Fruits.all.random()) }

    // How much the falling fruit is turned, in degrees.
    var fruitAngle by remember { mutableStateOf(0f) }

    // All cut animations that are running right now (dagger, halves, water).
    val cuts = remember { mutableStateListOf<FruitCut>() }

    // The bomb that is falling right now. null means the falling item is a fruit.
    var currentBomb by remember { mutableStateOf<BombType?>(null) }

    // All explosions that are running right now.
    val blasts = remember { mutableStateListOf<Blast>() }

    // CAMERA SHAKE. shakeX / shakeY = how far the picture is pushed right now.
    // The other three describe the running shake: power, time left, total time.
    var shakeX by remember { mutableStateOf(0f) }
    var shakeY by remember { mutableStateOf(0f) }
    var shakePower by remember { mutableStateOf(0f) }
    var shakeLeft by remember { mutableStateOf(0f) }
    var shakeTotal by remember { mutableStateOf(0.001f) }

    // How full the restart "hold" is, 0.0 to 1.0. Used to color the button while your hand is on it.
    var restartProgress by remember { mutableStateOf(0f) }

    // Convert the button settings from dp to real pixels (hand points are measured in pixels).
    val density = LocalDensity.current
    val buttonSizePx = with(density) { RESTART_SIZE_DP.dp.toPx() }
    val buttonTopPx = with(density) { RESTART_MARGIN_TOP_DP.dp.toPx() }
    val buttonEndPx = with(density) { RESTART_MARGIN_END_DP.dp.toPx() }

    // SPAWN BALL: puts a new item at a random spot at the top.
    // Usually a random fruit, sometimes a bomb (but never before BOMBS_START_SCORE points).
    val spawnBall = {
        if (score >= BOMBS_START_SCORE && Random.nextFloat() < BOMB_CHANCE) {
            currentBomb = Bombs.random()
        } else {
            currentBomb = null
            currentFruit = Fruits.all.random()
        }
        ballX = randomBallX(boxSize.width)
        ballY = -BALL_RADIUS
    }

    // START SHAKE: shakes the camera picture. A weaker shake never replaces a stronger one.
    val startShake = { power: Float, seconds: Float ->
        val strengthNow = shakePower * (shakeLeft / shakeTotal).coerceIn(0f, 1f)
        if (power >= strengthNow) {
            shakePower = power
            shakeTotal = seconds
            shakeLeft = seconds
        }
    }

    // RESTART: puts everything back to the start. Used by the touch button,
    // by the hand on the button, and by the "Play again" button on the game over box.
    val restartGame = {
        score = 0
        lives = START_LIVES
        recordAnnounced = false
        cuts.clear()
        blasts.clear()
        shakeLeft = 0f
        sounds.playRestart()
        spawnBall()
    }

    // ---------------- MUSIC ----------------
    // Music plays while you have lives, and pauses on game over.
    LaunchedEffect(lives) {
        if (lives > 0) sounds.startMusic() else sounds.pauseMusic()
    }

    // Pause the music when the app goes to the background, continue when it comes back.
    // Also free the sounds when the screen closes.
    DisposableEffect(Unit) {
        val lifecycle = (context as LifecycleOwner).lifecycle

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                sounds.pauseMusic()
            }
            if (event == Lifecycle.Event.ON_RESUME && latestLives > 0) {
                sounds.startMusic()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            sounds.release()
        }
    }

    // The game loop below runs for a long time, so it must not hold an OLD handResult.
    // rememberUpdatedState always gives us the newest value when we read it.
    val latestHand by rememberUpdatedState(handResult)


    // ---------------- GAME LOOP ----------------
    // Runs once the screen size is known. Runs again if the size changes.
    LaunchedEffect(boxSize) {

        // Screen not measured yet, nothing to do.
        if (boxSize.width == 0 || boxSize.height == 0) return@LaunchedEffect

        // Put the first ball at the top.
        spawnBall()

        var lastFrameTime = 0L

        // Loop forever (it stops automatically when the screen is closed).
        while (true) {

            // withFrameNanos waits for the next screen refresh (about 60 times a second).
            withFrameNanos { frameTime ->

                // How many seconds passed since the last frame.
                if (lastFrameTime == 0L) lastFrameTime = frameTime
                val seconds = (frameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = frameTime

                // HAND ON THE RESTART BUTTON?
                // The button is a circle: find its center and radius in pixels.
                val buttonRadius = buttonSizePx / 2f
                val buttonCenterX = boxSize.width - buttonEndPx - buttonRadius
                val buttonCenterY = buttonTopPx + buttonRadius

                // The button only exists on game over, so only then can the hand press it.
                val handOnButton = lives <= 0 && isHandInCircle(
                    result = latestHand,
                    centerX = buttonCenterX,
                    centerY = buttonCenterY,
                    radius = buttonRadius,
                    width = boxSize.width,
                    height = boxSize.height
                )

                if (handOnButton) {
                    // Count how long the hand stays on the button.
                    restartProgress += seconds
                    if (restartProgress >= HOLD_TO_RESTART_SECONDS) {
                        restartProgress = 0f
                        restartGame()
                    }
                } else {
                    restartProgress = 0f   // hand left, start over
                }

                // Move all cut animations forward in time and remove the finished ones.
                for (cut in cuts) {
                    cut.age += seconds
                }
                cuts.removeAll { it.age > CUT_DURATION }

                // Same for explosions.
                for (blast in blasts) {
                    blast.age += seconds
                }
                blasts.removeAll { it.age > it.duration }

                // Camera shake: push the picture a random bit every frame, weaker and weaker.
                if (shakeLeft > 0f) {
                    shakeLeft -= seconds
                    val strength = shakePower * (shakeLeft / shakeTotal).coerceIn(0f, 1f)
                    shakeX = (Random.nextFloat() * 2f - 1f) * strength
                    shakeY = (Random.nextFloat() * 2f - 1f) * strength
                } else {
                    shakeX = 0f
                    shakeY = 0f
                }

                // GAME OVER: freeze the ball and wait for the restart button.
                if (lives <= 0) return@withFrameNanos

                // Ball gets faster as the score grows, but never above the limit.
                val speed = minOf(
                    START_FALL_SPEED + score * SPEED_PER_POINT,
                    MAX_FALL_SPEED
                )

                // 1) Move the ball down.
                ballY += speed * seconds
                fruitAngle += FRUIT_SPIN * seconds

                // 2) Did a hand touch the item? Bombs can be bigger than fruits.
                val bomb = currentBomb
                val itemRadius = BALL_RADIUS * (bomb?.sizeScale ?: 1f)

                val caught = isBallTouched(
                    result = latestHand,
                    ballX = ballX,
                    ballY = ballY,
                    radius = itemRadius,
                    width = boxSize.width,
                    height = boxSize.height
                )

                if (caught && bomb != null) {
                    // ---------- BOOM! A bomb was cut ----------
                    blasts.add(Blast(bomb, ballX, ballY, itemRadius))
                    startShake(bomb.shakePower, bomb.shakeSeconds)

                    // Lose lives. The mega bomb takes all of them = game over.
                    lives = (lives - bomb.livesLost).coerceAtLeast(0)

                    sounds.playFall()
                    if (lives <= 0) {
                        // No lives left: game over sound, hide the bomb, everything freezes.
                        sounds.playGameOver()
                        ballY = -itemRadius * 2f
                    } else {
                        // Still alive: sad sound and a new item.
                        sounds.playLifeLost()
                        spawnBall()
                    }

                } else if (caught) {
                    // ---------- A fruit was cut ----------
                    // Add a point.
                    score = score + 1

                    // Start the cut animation where the fruit is: dagger, halves, water.
                    cuts.add(FruitCut(currentFruit, ballX, ballY, BALL_RADIUS, fruitAngle))

                    // Small camera shake for every cut.
                    startShake(CUT_SHAKE_POWER, CUT_SHAKE_SECONDS)

                    // Dagger swoosh and the pop of the cut.
                    sounds.playSwoosh()
                    sounds.playCatch()

                    // One extra sound on top, the most special one wins:
                    //   passed your old high score (once per game) -> fanfare
                    //   every 10th point                           -> level up
                    //   every 4th point                            -> plus four ping
                    val passedOldRecord = score > highScore && highScore > 0 && !recordAnnounced
                    if (passedOldRecord) {
                        sounds.playHighScore()
                        recordAnnounced = true
                    } else if (score % 10 == 0) {
                        sounds.playLevelUp()
                    } else if (score % 4 == 0) {
                        sounds.playScorePlusFour()
                    }

                    // Beat the old record? Then this score is the new high score.
                    if (score > highScore) {
                        highScore = score
                    }

                    // Send a new item from the top.
                    spawnBall()

                } else if (ballY - itemRadius > boxSize.height) {
                    // The item fell off the bottom.
                    if (bomb != null) {
                        // A bomb that you did not touch is GOOD. Nothing happens, next item.
                        spawnBall()
                    } else {
                        // A missed fruit costs one life.
                        lives = lives - 1
                        sounds.playFall()

                        if (lives <= 0) {
                            // Last life gone: game over sound, hide the fruit, everything freezes.
                            sounds.playGameOver()
                            ballY = -itemRadius * 2f
                        } else {
                            // Still alive: sad sound and a new item.
                            sounds.playLifeLost()
                            spawnBall()
                        }
                    }
                }
            }
        }
    }


    // ---------------- UI ----------------
    // Box stacks things on top of each other: camera at the bottom,
    // then the ball, then the score text.
    Box(
        modifier = modifier.onSizeChanged { newSize ->
            boxSize = newSize          // remember how big the screen area is
        }
    ) {

        // LAYER 1 + 2 sit together in their own Box, so the camera picture
        // AND everything drawn on it shake together (camera shake!).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = shakeX
                    translationY = shakeY
                }
        ) {

            // LAYER 1: the camera picture.
            CameraView(helper = helper)

            // LAYER 2: the falling item, the cut animations and the explosions.
            Canvas(modifier = Modifier.fillMaxSize()) {

                val bomb = currentBomb

                if (bomb != null) {
                    // A bomb (does not turn, so the fuse stays on top).
                    translate(ballX, ballY) {
                        drawBomb(bomb, BALL_RADIUS * bomb.sizeScale, fruitAngle)
                    }
                } else {
                    // A fruit: move to its position, turn it, draw it.
                    translate(ballX, ballY) {
                        rotate(degrees = fruitAngle, pivot = Offset.Zero) {
                            currentFruit.draw(this, BALL_RADIUS)
                        }
                    }
                }

                // Every running cut animation.
                for (cut in cuts) {
                    drawFruitCut(cut)
                }

                // Every running explosion (drawn last = on top of everything).
                for (blast in blasts) {
                    drawBlast(blast)
                }
            }
        }

        // LAYER 3: the info panel at the top center.
        // One single row (Score | Lives | Best) so it is wide but NOT tall,
        // which keeps the play area big.
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(
                    Color.Black.copy(alpha = 0.65f),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),   // gap between items
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Item 1: current score.
            Text(
                text = "Score: $score",
                color = Color.White,
                fontSize = 22.sp
            )

            // Item 2: lives. 3 or more lives = green. Only 1 or 2 left = red alert.
            val livesColor = if (lives >= 3) Color(0xFF4CAF50) else Color(0xFFFF5252)
            val livesText = if (lives >= 3) "Lives: $lives" else "\u26A0\uFE0F Lives: $lives"
            Text(
                text = livesText,
                color = livesColor,
                fontSize = 22.sp
            )

            // Item 3: high score. Sunglasses when you are AT the record right now,
            // trophy otherwise.
            val atRecord = score > 0 && score >= highScore
            val bestEmoji = if (atRecord) "\uD83D\uDE0E" else "\uD83C\uDFC6"
            Text(
                text = "$bestEmoji Best: $highScore",
                color = Color.Yellow,
                fontSize = 22.sp
            )

            // Item 4: hand status (moved here from MainActivity).
            // No hand seen by the camera = red message. Hand seen = green "Hand OK".
            val handVisible = handResult?.landmarks()?.isEmpty() == false
            Text(
                text = if (handVisible) "\u270B Hand OK" else "\u270B Show your hand",
                color = if (handVisible) Color(0xFF4CAF50) else Color(0xFFFF5252),
                fontSize = 22.sp
            )
        }

        // LAYER 4: ROUND RESTART button in the top right corner.
        // It only appears when the game is over.
        // Works by touch AND by holding your hand on it.
        // While your hand is on it the button slowly turns green (that is the hold timer).
        if (lives <= 0) {
            val holdFraction = if (HOLD_TO_RESTART_SECONDS > 0f)
                (restartProgress / HOLD_TO_RESTART_SECONDS).coerceIn(0f, 1f) else 0f
            Button(
                onClick = {
                    sounds.playUiTouch()
                    restartGame()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = RESTART_MARGIN_TOP_DP.dp, end = RESTART_MARGIN_END_DP.dp)
                    .size(RESTART_SIZE_DP.dp),
                shape = CircleShape,                       // makes it round
                contentPadding = PaddingValues(0.dp),      // so the icon fits inside the circle
                colors = ButtonDefaults.buttonColors(
                    containerColor = lerp(
                        Color.Black.copy(alpha = 0.65f),
                        Color(0xFF4CAF50),
                        holdFraction
                    ),
                    contentColor = Color.White
                )
            ) {
                Text("\uD83D\uDD04", fontSize = 40.sp)
            }
        }

        // LAYER 5: GAME OVER box. Only shown when lives are finished.
        if (lives <= 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        Color(0xFF1B1B2F).copy(alpha = 0.92f),     // dark blue card
                        RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFF5252),                  // red border
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 48.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Big title.
                Text(
                    text = "\uD83D\uDC80 GAME OVER",
                    color = Color(0xFFFF5252),
                    fontSize = 36.sp
                )

                // Final score, big and white.
                Text(
                    text = "Score: $score",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // New record message, or the current best.
                val beatRecord = score > 0 && score >= highScore
                Text(
                    text = if (beatRecord) "\uD83D\uDE0E New high score!"
                    else "\uD83C\uDFC6 Best: $highScore",
                    color = Color.Yellow,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Big green play again button.
                Button(
                    onClick = {
                        sounds.playUiTouch()
                        restartGame()
                    },
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
//   Your old camera setup code, in its own function.
//   NEW: it also tells the frame grabber when the phone is turned
//   (for example landscape flipped upside down), so MediaPipe always
//   gets an upright picture.
// ---------------------------------------------------------------------
@Composable
private fun CameraView(helper: HandLandmarkerHelper) {

    val context = LocalContext.current

    // The camera picture view and the frame grabber are created once and kept,
    // so the rotation listener below can reach them.
    val previewView = remember { PreviewView(context) }

    val analyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    }

    // Watches the screen rotation and passes it to the frame grabber.
    DisposableEffect(Unit) {

        val displayManager =
            context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val listener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {}
            override fun onDisplayRemoved(displayId: Int) {}
            override fun onDisplayChanged(displayId: Int) {
                val display = previewView.display
                if (display != null) {
                    analyzer.targetRotation = display.rotation
                }
            }
        }

        displayManager.registerDisplayListener(listener, null)

        // Stop watching when this screen is closed.
        onDispose { displayManager.unregisterDisplayListener(listener) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()

                // The live camera picture.
                val preview = Preview.Builder().build()
                preview.surfaceProvider = previewView.surfaceProvider

                // Make sure the frame grabber starts with the current screen rotation.
                previewView.display?.let { analyzer.targetRotation = it.rotation }

                analyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    HandAnalyzer(helper)
                )

                // Front camera (selfie). Use DEFAULT_BACK_CAMERA for the back one.
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    ctx as LifecycleOwner,
                    cameraSelector,
                    preview,
                    analyzer
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}


// ---------------------------------------------------------------------
// randomBallX
//   Picks a random horizontal spot for a new ball,
//   keeping the whole circle inside the screen.
// ---------------------------------------------------------------------
private fun randomBallX(width: Int): Float {
    val min = BALL_RADIUS * 1.4f          // keeps even the big bomb fully on screen
    val max = width - BALL_RADIUS * 1.4f
    return Random.nextFloat() * (max - min) + min
}


// ---------------------------------------------------------------------
// isBallTouched
//   Returns true if ANY of the 21 points of ANY detected hand
//   is inside (or very near) the ball.
//
//   MediaPipe gives each point as 0.0 to 1.0 (a fraction of the picture),
//   so we multiply by the screen size to get real pixels.
//   X is flipped (1 - x) because the front camera is mirrored.
//   This is the same math HandOverlay uses, so the dots and the ball line up.
// ---------------------------------------------------------------------
private fun isBallTouched(
    result: HandLandmarkerResult?,
    ballX: Float,
    ballY: Float,
    radius: Float,
    width: Int,
    height: Int
): Boolean {

    // No hand detected right now.
    if (result == null) return false

    val reach = radius + CATCH_EXTRA

    for (hand in result.landmarks()) {          // each hand (max 2)
        for (point in hand) {                   // each of its 21 points

            val pointX = (1f - point.x()) * width
            val pointY = point.y() * height

            // Distance between the point and the ball center (Pythagoras).
            val dx = pointX - ballX
            val dy = pointY - ballY
            val distanceSquared = dx * dx + dy * dy

            // Compare squared values to avoid a slow square root.
            if (distanceSquared <= reach * reach) {
                return true
            }
        }
    }

    return false
}


// ---------------------------------------------------------------------
// isHandInCircle
//   Returns true if ANY point of ANY hand is inside a circle on the screen.
//   Same pixel + mirror math as isBallTouched. Used for the round restart button.
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

            val pointX = (1f - point.x()) * width
            val pointY = point.y() * height

            // Distance from the circle center (Pythagoras, squared to skip the root).
            val dx = pointX - centerX
            val dy = pointY - centerY

            if (dx * dx + dy * dy <= radius * radius) {
                return true
            }
        }
    }

    return false
}