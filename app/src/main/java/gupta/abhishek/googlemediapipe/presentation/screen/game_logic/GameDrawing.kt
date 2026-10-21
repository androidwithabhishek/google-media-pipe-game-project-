package gupta.abhishek.googlemediapipe.presentation.screen.game_logic

import gupta.abhishek.googlemediapipe.presentation.BombType
import gupta.abhishek.googlemediapipe.presentation.FruitCut
import gupta.abhishek.googlemediapipe.presentation.drawBomb
import gupta.abhishek.googlemediapipe.presentation.drawFruitCut



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import gupta.abhishek.googlemediapipe.core.fruits.Blast
import gupta.abhishek.googlemediapipe.core.fruits.FruitType
import gupta.abhishek.googlemediapipe.core.fruits.drawBlast

// ---------------------------------------------------------------------
// drawGameItems
//   Draws the falling item (bomb or fruit), then cut animations, then blasts.
//   Order matters: later = on top.
// ---------------------------------------------------------------------
internal fun DrawScope.drawGameItems(
    bomb: BombType?,
    fruit: FruitType,
    itemX: Float,
    itemY: Float,
    fruitSpinAngle: Float,
    cuts: List<FruitCut>,
    blasts: List<Blast>
) {
    if (bomb != null) {
        // Bombs do not spin, so the fuse stays on top.
        translate(itemX, itemY) {
            drawBomb(bomb, FRUIT_BASE_RADIUS_PX * bomb.sizeScale, fruitSpinAngle)
        }
    } else {
        translate(itemX, itemY) {
            rotate(degrees = fruitSpinAngle, pivot = Offset.Zero) {
                fruit.draw(this, fruitRadiusFor(fruit))
            }
        }
    }

    for (cut in cuts) {
        drawFruitCut(cut)
    }

    for (blast in blasts) {
        drawBlast(blast)
    }
}