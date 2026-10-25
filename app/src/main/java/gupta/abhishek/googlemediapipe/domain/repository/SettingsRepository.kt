package gupta.abhishek.googlemediapipe.domain.repository

import gupta.abhishek.googlemediapipe.domain.model.PlayerSettings
import kotlinx.coroutines.flow.Flow

// The app talks to this interface only. Room is hidden in the data layer.
interface SettingsRepository {

    // Emits the current settings and again whenever they change.
    fun observeSettings(): Flow<PlayerSettings>

    // Turns the camera and hand gestures on or off.
    suspend fun setHandControlEnabled(enabled: Boolean)

    // Remembers that the one-time hand instruction was shown.
    suspend fun markHandInstructionSeen()
}
