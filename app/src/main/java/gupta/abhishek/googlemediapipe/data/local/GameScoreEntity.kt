package gupta.abhishek.googlemediapipe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// One row = one finished game.
@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val playedAtMillis: Long
)
