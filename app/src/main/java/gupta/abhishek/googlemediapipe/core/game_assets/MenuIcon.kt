import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import gupta.abhishek.googlemediapipe.core.game_assets.engraved

// ---------------------------------------------------------------------
// drawMenuIcon
//   A bullet-list style menu: a dot, then a bar, three times.
//   Engraved into the 3D yellow button.
//   Paints around (0, 0). "r" is the radius of the BUTTON.
// ---------------------------------------------------------------------
fun DrawScope.drawMenuIcon(r: Float) {

    engraved(r) { color ->

        val gap = 0.30f * r            // distance from the middle row to the top and bottom rows
        val thickness = 0.16f * r      // bar thickness
        val dotRadius = 0.09f * r      // a touch bigger than the bar's half-thickness

        // Whole icon spans -0.40r .. +0.40r, so it stays centered.
        val dotX = -0.31f * r          // dot center
        val barStart = -0.04f * r      // round cap adds thickness/2 on each end
        val barEnd = 0.32f * r

        for (y in listOf(-gap, 0f, gap)) {

            // The dot.
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(dotX, y)
            )

            // The bar after it, with round ends.
            drawLine(
                color = color,
                start = Offset(barStart, y),
                end = Offset(barEnd, y),
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
        }
    }
}