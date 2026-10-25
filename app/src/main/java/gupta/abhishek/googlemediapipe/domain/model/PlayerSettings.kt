package gupta.abhishek.googlemediapipe.domain.model

// What the player can change in the app. These are the defaults for a new install.
data class PlayerSettings(
    val handControlEnabled: Boolean = true,
    val handInstructionSeen: Boolean = false
)
