package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.fruits.Blast
import gupta.abhishek.googlemediapipe.core.fruits.Fruits
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.BOMBS_UNLOCK_SCORE
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.BOMB_SPAWN_CHANCE
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CUT_SHAKE_DURATION_SECONDS
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CUT_SHAKE_STRENGTH_PX
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.CameraView
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.FALL_SPEED_INCREASE_PER_POINT
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.FRUIT_BASE_RADIUS_PX
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.FRUIT_SPIN_DEGREES_PER_SECOND
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GameHud
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.GameOverCard
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.MAX_FALL_SPEED_PX_PER_SECOND
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.PauseOverlay
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_MARGIN_END_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_MARGIN_TOP_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_BUTTON_SIZE_DP
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RESTART_HOLD_SECONDS
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.RestartButton
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.STARTING_LIVES
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.START_FALL_SPEED_PX_PER_SECOND
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.drawGameItems
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.fruitRadiusFor
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.isHandInCircle
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.isItemTouchedByHand
import gupta.abhishek.googlemediapipe.presentation.screen.game_logic.randomItemX
import kotlin.random.Random

// ---------------------------------------------------------------------
// CameraPreview
//   Shows the camera, drops fruits and bombs on top of it,
//   and scores a point when a hand touches a falling fruit.
//
//   helper     -> MediaPipe helper that receives camera frames
//   handResult -> latest hand detection result from MainActivity
//
//   The pieces live in other files:
//   GameSettings.kt  (numbers), GameHelpers.kt (size and hit checks),
//   CameraView.kt (camera), GameDrawing.kt (drawing),
//   GameOverlay.kt (score, hearts, pause, restart, game over)
// ---------------------------------------------------------------------
@Composable
fun CameraPreview(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    modifier: Modifier = Modifier,
    bestScoreFromDatabase: Int = 0,        // best score saved in the database
    onGameOver: (Int) -> Unit = {}         // called with the final score when the game ends
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

    // True while the game is paused (pause button, bottom right).
    var isPaused by remember { mutableStateOf(false) }

    // How long the hand has been on the restart button, in seconds.
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
        isPaused = false
        runningCuts.clear()
        runningBlasts.clear()
        shakeSecondsLeft = 0f
        dropNextItem()
    }

    // Pauses or continues the game. Any running camera shake is stopped so the picture stays still.
    val togglePause = {
        isPaused = !isPaused
        shakeSecondsLeft = 0f
        shakeOffsetX = 0f
        shakeOffsetY = 0f
    }

    // The game loop runs for a long time, so it must always read the newest hand result.
    val latestHandResult by rememberUpdatedState(handResult)

    // Use the saved best score when it is higher than the one on screen.
    LaunchedEffect(bestScoreFromDatabase) {
        if (bestScoreFromDatabase > bestScore) bestScore = bestScoreFromDatabase
    }

    // Report the final score once when the game ends (lives reach 0).
    val latestOnGameOver by rememberUpdatedState(onGameOver)
    LaunchedEffect(livesLeft) {
        if (livesLeft <= 0) latestOnGameOver(currentScore)
    }


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

                // ----- Paused: freeze everything (time still passes, so nothing jumps on resume) -----
                if (isPaused) return@withFrameNanos

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
                drawGameItems(
                    bomb = currentBomb,
                    fruit = currentFruit,
                    itemX = itemX,
                    itemY = itemY,
                    fruitSpinAngle = fruitSpinAngle,
                    cuts = runningCuts,
                    blasts = runningBlasts
                )
            }
        }

        // Dark screen with Play / Replay buttons while paused.
        // It is placed BEFORE the HUD so the small pause button stays on top and works as Play.
        if (isPaused && livesLeft > 0) {
            PauseOverlay(
                onResume = { togglePause() },
                onRestart = { restartGame() }
            )
        }

        // Score and best (top left), hearts (top center), pause button (bottom right).
        GameHud(
            currentScore = currentScore,
            livesLeft = livesLeft,
            bestScore = bestScore,
            handResult = handResult,
            isPaused = isPaused,
            showPauseButton = livesLeft > 0,
            onPauseToggle = { togglePause() }
        )

        // Restart button and game over card, only on game over.
        if (livesLeft <= 0) {
            RestartButton(
                restartHoldProgress = restartHoldProgress,
                onRestart = { restartGame() }
            )

            GameOverCard(
                currentScore = currentScore,
                bestScore = bestScore,
                onRestart = { restartGame() }
            )
        }
    }
}