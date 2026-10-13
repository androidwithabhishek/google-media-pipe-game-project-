package gupta.abhishek.googlemediapipe.core.fruits

import gupta.abhishek.googlemediapipe.core.fruit_type.FruitType
import gupta.abhishek.googlemediapipe.core.fruit_type.Orange
import gupta.abhishek.googlemediapipe.presentation.Apple
import gupta.abhishek.googlemediapipe.presentation.Banana
import gupta.abhishek.googlemediapipe.presentation.Mango
import gupta.abhishek.googlemediapipe.presentation.Watermelon

object Fruits {

    // The game picks a random fruit from this list.
    // To add a fruit: create its file (like Apple.kt), then add "MyFruit.type" here.
    val all: List<FruitType> = listOf(


        Apple.type,
        Watermelon.type,
        Orange.type,
        Mango.type,
        Banana.type,

    )
}