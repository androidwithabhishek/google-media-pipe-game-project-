package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// One row only (id = 1). Holds the player's settings.
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val handControlEnabled: Boolean,      // true = camera and hand gestures on
    val handInstructionSeen: Boolean      // true = the one-time hand instruction was shown
)
