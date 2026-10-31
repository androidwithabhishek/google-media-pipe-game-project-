package gupta.abhishek.googlemediapipe.domain.model


data class PlayerSettings(
    val handControlEnabled: Boolean = false,
    val handInstructionSeen: Boolean = false,
    val soundEnabled: Boolean = true,    // sound effects (cut, bombs, buttons)
    val musicEnabled: Boolean = true     // background music
)