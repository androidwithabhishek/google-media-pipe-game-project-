package gupta.abhishek.googlemediapipe.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// One row only (id = 1). Holds all player settings.
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val handControlEnabled: Boolean,      // true = camera and hand gestures on
    val handInstructionSeen: Boolean,     // true = the one-time hand instruction was shown
    @ColumnInfo(defaultValue = "1") val soundEnabled: Boolean = true,   // sound effects on/off
    @ColumnInfo(defaultValue = "1") val musicEnabled: Boolean = true    // background music on/off
)