package gupta.abhishek.googlemediapipe.presentation.screen.game_logic


import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import kotlin.random.Random

// ---------------------------------------------------------------------
// fruitRadiusFor
//   The real on-screen radius of a fruit, in pixels.
//   Use this everywhere a fruit is drawn, cut, or hit-checked.
// ---------------------------------------------------------------------
internal fun fruitRadiusFor(fruit: FruitType): Float =
    FRUIT_BASE_RADIUS_PX * fruit.sizeScale * FRUIT_SIZE_MULTIPLIER

// ---------------------------------------------------------------------
// randomItemX
//   Random horizontal spot for a new item, keeping it fully on screen.
// ---------------------------------------------------------------------
internal fun randomItemX(screenWidthPx: Int): Float {
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
internal fun isItemTouchedByHand(
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
internal fun isHandInCircle(
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