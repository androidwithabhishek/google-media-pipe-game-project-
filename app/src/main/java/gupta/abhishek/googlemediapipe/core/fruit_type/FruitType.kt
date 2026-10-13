package gupta.abhishek.googlemediapipe.core.fruit_type

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

class FruitType(
    val name: String,
    val skinColor: Color,
    val juiceColor: Color,
    val insideColor: Color,
    val seedColor: Color = Color(0xFF4E342E),
    val seedCount: Int = 3,
    val sizeScale: Float = 1f,
    val draw: DrawScope.(radius: Float) -> Unit
)