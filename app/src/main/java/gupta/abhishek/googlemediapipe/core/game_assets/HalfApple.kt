//package gupta.abhishek.googlemediapipe.core.game_assets
//
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.rotate
//import androidx.compose.ui.graphics.drawscope.scale
//import androidx.compose.ui.graphics.drawscope.translate
//
//// ---------------------------------------------------------------------
//// Pear outline: narrow neck at the top, wide round bottom.
//// Centered on (0, 0).
//// ---------------------------------------------------------------------
//private fun halfPearBodyPath(r: Float): Path = Path().apply {
//    moveTo(0f, -1.0f * r)
//    cubicTo(0.22f * r, -1.0f * r, 0.28f * r, -0.6f * r, 0.32f * r, -0.45f * r)
//    cubicTo(0.45f * r, -0.25f * r, 0.88f * r, -0.1f * r, 0.88f * r, 0.4f * r)
//    cubicTo(0.88f * r, 0.9f * r, 0.5f * r, 1.05f * r, 0.28f * r, 0.95f * r)
//    cubicTo(0.12f * r, 0.88f * r, 0.05f * r, 0.72f * r, 0f, 0.75f * r)
//    cubicTo(-0.05f * r, 0.72f * r, -0.12f * r, 0.88f * r, -0.28f * r, 0.95f * r)
//    cubicTo(-0.5f * r, 1.05f * r, -0.88f * r, 0.9f * r, -0.88f * r, 0.4f * r)
//    cubicTo(-0.88f * r, -0.1f * r, -0.45f * r, -0.25f * r, -0.32f * r, -0.45f * r)
//    cubicTo(-0.28f * r, -0.6f * r, -0.22f * r, -1.0f * r, 0f, -1.0f * r)
//    close()
//}
//
//// Pear seed: a small teardrop, pointing down.
//private fun DrawScope.drawHalfPearSeed(r: Float, color: Color) {
//    val seed = Path().apply {
//        moveTo(0f, -0.05f * r)
//        cubicTo(0.04f * r, -0.02f * r, 0.04f * r, 0.06f * r, 0f, 0.08f * r)
//        cubicTo(-0.04f * r, 0.06f * r, -0.04f * r, -0.02f * r, 0f, -0.05f * r)
//        close()
//    }
//    drawPath(path = seed, color = color)
//}
//
//// Leaf: two curves meeting at a pointed tip, attached near the stem.
//private fun halfPearLeafPath(r: Float): Path = Path().apply {
//    moveTo(0.05f * r, -1.15f * r)
//    cubicTo(0.22f * r, -1.50f * r, 0.48f * r, -1.50f * r, 0.65f * r, -1.32f * r)
//    cubicTo(0.48f * r, -1.15f * r, 0.25f * r, -1.08f * r, 0.05f * r, -1.15f * r)
//    close()
//}
//
//// ---------------------------------------------------------------------
//// drawHalfPear
////   A pear cut in half: pale yellow-green skin, cream flesh, a small
////   core with two dark seeds, a stem and a leaf, all tilted by [tiltDegrees].
////   Paints around (0, 0). "r" is the size of the pear.
//// ---------------------------------------------------------------------
//fun DrawScope.drawHalfPear(r: Float, tiltDegrees: Float = -30f) {
//
//    rotate(degrees = tiltDegrees, pivot = Offset.Zero) {
//
//        val body = halfPearBodyPath(r)
//
//        // 1. Drop shadow: same shape, black and see-through, offset down and right.
//        translate(0.04f * r, 0.07f * r) {
//            drawPath(path = body, color = Color.Black.copy(alpha = 0.35f))
//        }
//
//        // 2. Skin: pale yellow-green, a little greener toward the lower right.
//        drawPath(
//            path = body,
//            brush = Brush.radialGradient(
//                colors = listOf(Color(0xFFF4F8C8), Color(0xFFD4E157), Color(0xFF9CB53A)),
//                center = Offset(-0.3f * r, -0.3f * r),
//                radius = 1.6f * r
//            )
//        )
//
//        // 3. Cream flesh: same shape at 90%, leaving a visible skin rim.
//        scale(scale = 0.90f, pivot = Offset.Zero) {
//            drawPath(
//                path = body,
//                brush = Brush.radialGradient(
//                    colors = listOf(Color(0xFFFFFDEB), Color(0xFFF9F3C8), Color(0xFFEFE49A)),
//                    center = Offset(-0.2f * r, -0.2f * r),
//                    radius = 1.4f * r
//                )
//            )
//        }
//
//        // 4. Core: a small pale cavity around the seeds.
//        drawOval(
//            color = Color(0xFFE8DCA0),
//            topLeft = Offset(-0.12f * r, -0.08f * r),
//            size = Size(0.24f * r, 0.32f * r)
//        )
//        drawOval(
//            color = Color(0xFFD2C280),
//            topLeft = Offset(-0.12f * r, -0.08f * r),
//            size = Size(0.24f * r, 0.32f * r),
//            style = Stroke(width = 0.015f * r)
//        )
//
//        // 5. Seeds: two dark, angled teardrops at the center of the core.
//        val seedColor = Color(0xFF3E2723)
//        translate(-0.05f * r, 0.08f * r) {
//            rotate(degrees = -20f) { drawHalfPearSeed(r, seedColor) }
//        }
//        translate(0.05f * r, 0.08f * r) {
//            rotate(degrees = 20f) { drawHalfPearSeed(r, seedColor) }
//        }
//
//        // 6. Highlights: a soft one upper left, a smaller one lower right.
//        drawOval(
//            color = Color.White.copy(alpha = 0.5f),
//            topLeft = Offset(-0.6f * r, -0.4f * r),
//            size = Size(0.2f * r, 0.4f * r)
//        )
//        drawOval(
//            color = Color.White.copy(alpha = 0.3f),
//            topLeft = Offset(0.45f * r, 0.4f * r),
//            size = Size(0.14f * r, 0.25f * r)
//        )
//
//        // 7. Stem.
//        val stem = Path().apply {
//            moveTo(0f, -0.95f * r)
//            quadraticBezierTo(0.02f * r, -1.10f * r, 0.10f * r, -1.25f * r)
//        }
//        drawPath(
//            path = stem,
//            color = Color(0xFF5D4037),
//            style = Stroke(width = 0.06f * r, cap = StrokeCap.Round)
//        )
//
//        // 8. Leaf, attached near the stem.
//        drawPath(
//            path = halfPearLeafPath(r),
//            brush = Brush.linearGradient(
//                colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
//                start = Offset(0.05f * r, -1.15f * r),
//                end = Offset(0.65f * r, -1.32f * r)
//            )
//        )
//    }
//}