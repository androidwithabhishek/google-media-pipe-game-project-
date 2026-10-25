package gupta.abhishek.googlemediapipe.core.fonts



import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import gupta.abhishek.googlemediapipe.R


// All game fonts in one place. Use them anywhere with: fontFamily = GameFonts.FredokaFont
object GameFonts {



    val JungleRoarFont = FontFamily(
        Font(
            resId = R.font.jungle_roar_bold,
            weight = FontWeight.Bold
        )
    )


}