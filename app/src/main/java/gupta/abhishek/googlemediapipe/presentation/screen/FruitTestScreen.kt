package gupta.abhishek.googlemediapipe.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import gupta.abhishek.googlemediapipe.core.fruits.Fruits

// Test-only screen: shows ONE fruit big in the middle, so you can look at each fruit.
// Tap the fruit or press Cut to see its cut animation. Not used by the real game.
// Start it from MainActivity by setting SHOW_FRUIT_TEST = true.

private const val TEST_FRUIT_SPIN_DEGREES_PER_SECOND = 1f
private const val TEST_FRUIT_SIZE_FRACTION = 0.30f   // fruit size as a share of the screen's shorter side

private fun testFruitRadius(fruit: FruitType, shortSidePx: Float): Float =
    shortSidePx * TEST_FRUIT_SIZE_FRACTION * fruit.sizeScale

@Composable
fun FruitTestScreen(onGoToGame: () -> Unit = {}) {

    val fruits = Fruits.all

    var index by remember { mutableStateOf(0) }
    var spinAngle by remember { mutableStateOf(0f) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val cuts = remember { mutableStateListOf<FruitCut>() }
    var paused by remember { mutableStateOf(false) }
    val fruit = fruits[index]


    // Frame loop: keeps the fruit spinning and moves the cut animations forward.
    LaunchedEffect(Unit) {
        var previousNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (previousNanos == 0L) previousNanos = frameNanos
                val seconds = (frameNanos - previousNanos) / 1_000_000_000f
                previousNanos = frameNanos

                spinAngle += TEST_FRUIT_SPIN_DEGREES_PER_SECOND * seconds
                if (!paused) {
                    spinAngle += TEST_FRUIT_SPIN_DEGREES_PER_SECOND * seconds
                    for (cut in cuts) cut.age += seconds
                }
                cuts.removeAll { it.age > CUT_DURATION }
            }
        }
    }

    // Starts one cut animation on the current fruit (only if no cut is already running).
    fun cutCurrentFruit() {
        if (canvasSize.width == 0 || cuts.isNotEmpty()) return
        val shortSide = minOf(canvasSize.width, canvasSize.height).toFloat()
        cuts.add(
            FruitCut(
                type = fruit,
                centerX = canvasSize.width / 2f,
                centerY = canvasSize.height / 2f,
                radius = testFruitRadius(fruit, shortSide),
                rotation = spinAngle
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101418))
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .pointerInput(index) {
                    detectTapGestures { cutCurrentFruit() }
                }
        ) {
            // Show the whole fruit only when no cut is playing.
            if (cuts.isEmpty()) {
                val shortSide = minOf(size.width, size.height)
                translate(size.width / 2f, size.height / 2f) {
                    rotate(degrees = spinAngle, pivot = Offset.Zero) {
                        fruit.draw(this, testFruitRadius(fruit, shortSide))
                    }
                }
            }

            for (cut in cuts) {
                drawFruitCut(cut)
            }
        }

        // Fruit name and position, top center.
        Text(
            text = "${fruit.name.replaceFirstChar { it.uppercaseChar() }}   (${index + 1} / ${fruits.size})",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        // Buttons at the bottom.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                cuts.clear()
                index = (index - 1 + fruits.size) % fruits.size
            }) {
                Text("Prev", fontSize = 20.sp)
            }

            Button(onClick = { cutCurrentFruit() }) {
                Text("Cut", fontSize = 20.sp)
            }

            Button(onClick = {
                cuts.clear()
                index = (index + 1) % fruits.size
            }) {
                Text("Next", fontSize = 20.sp)
            }

            Button(onClick = { paused = !paused }) {
                Text(if (paused) "Play" else "Stop", fontSize = 20.sp)
            }

            Button(onClick = onGoToGame) {
                Text("Go to game", fontSize = 20.sp)
            }
        }
    }
}
