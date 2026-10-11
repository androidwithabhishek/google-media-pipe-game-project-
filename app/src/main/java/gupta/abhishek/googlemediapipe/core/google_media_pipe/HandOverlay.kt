package gupta.abhishek.googlemediapipe.core.google_media_pipe


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult


@Composable
fun HandOverlay(
    result: HandLandmarkerResult?,
    modifier: Modifier = Modifier,
) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        result?.landmarks()?.forEachIndexed { handIndex, hand ->

            hand.forEach { landmark ->

//                val x =
//                    landmark.x() * size.width
//
//                val y =
//                    landmark.y() * size.height

                val x = (1f - landmark.x()) * size.width
                val y = landmark.y() * size.height

//                YE DOORI ke liye hai
                val z = landmark.z()

                Log.d(
                    "HAND_Z",
                    "Hand=$handIndex X=$x Y=$y Z=$z"
                )

                drawCircle(
                    color = Color.Blue,
                    radius = 8f,
                    center = Offset(x, y),
                    alpha = 1.0f,
                    style = Fill,
                    colorFilter = null,
                    blendMode = BlendMode.SrcOver
                )
            }

            drawConnections(hand)
        }
    }
}

private fun DrawScope.drawConnections(
    landmarks: List<NormalizedLandmark>,
) {

    val connections = listOf(

        // Thumb
        0 to 1,
        1 to 2,
        2 to 3,
        3 to 4,

        // Index
        0 to 5,
        5 to 6,
        6 to 7,
        7 to 8,

        // Middle
        0 to 9,
        9 to 10,
        10 to 11,
        11 to 12,

        // Ring
        0 to 13,
        13 to 14,
        14 to 15,
        15 to 16,

        // Pinky
        0 to 17,
        17 to 18,
        18 to 19,
        19 to 20,

        // Palm
        5 to 9,
        9 to 13,
        13 to 17
    )

    connections.forEach { (start, end) ->

//        val startPoint =
//            Offset(
//                landmarks[start].x() * size.width,
//                landmarks[start].y() * size.height
//            )
//
//        val endPoint =
//            Offset(
//                landmarks[end].x() * size.width,
//                landmarks[end].y() * size.height
//            )

        val startPoint = Offset(
            x = (1f - landmarks[start].x()) * size.width,
            y = landmarks[start].y() * size.height
        )

        val endPoint = Offset(
            x = (1f - landmarks[end].x()) * size.width,
            y = landmarks[end].y() * size.height
        )

        drawLine(
            color = Color.Red,
            start = startPoint,
            end = endPoint,
            strokeWidth = 4f,
            cap = StrokeCap.Round,
            pathEffect = null,
            alpha = 1.0f,
            colorFilter = null,
            blendMode = BlendMode.SrcOver
        )
    }
}