package gupta.abhishek.googlemediapipe.core.sound


import android.media.MediaPlayer
import gupta.abhishek.googlemediapipe.R

// ----------------------------------------nu-----------------------------
// SoundEffect
//   One game sound and how it plays.
//   resId          -> the file in res/raw (null = no file yet, the event stays silent)
//   volume         -> 0.0 (silent) to 1.0 (full)
//   maxMillis      -> stop the sound after this many milliseconds (0 = play the whole file)
//   cooldownMillis -> wait this long before the same sound may start again
// ---------------------------------------------------------------------
class SoundEffect(
    val resId: Int?,
    val volume: Float = 1f,
    val maxMillis: Long = 0L,
    val cooldownMillis: Long = 0L,
    val pausable: Boolean = true    // false = keeps playing while the game is paused (button sounds)
)

// ---------------------------------------------------------------------
// GameSoundSettings
//   Every game sound is set here. Change the numbers here, not in the game code.
//   To cut a sound short, set maxMillis. To play it fully, set maxMillis to 0.
// ---------------------------------------------------------------------
object GameSoundSettings {

    // How many sounds may play at the same time.
    const val MAX_SIMULTANEOUS_SOUNDS = 8

    // Background music (looping). null = no music file yet.
    val backgroundMusicResId: Int = R.raw.game_background_loop


    const val MUSIC_VOLUME = 0.2f

    // ----- Fruit cut -----
    val fruitCut = SoundEffect(
        resId = R.raw.dagger_cut_with_fruit_cut_sound,
        volume = 0.9f,
        maxMillis = 1200L,       // stop after 1.2 seconds (try 0 to play it fully)
        cooldownMillis = 80L     // fast cuts do not stack the sound
    )

    // ----- Bombs -----
    val smallBombBlast = SoundEffect(
        resId = R.raw.bomb_blast_sound,
        volume = 1f,
        maxMillis = 1800L
    )

    val bigBombBlast = SoundEffect(
        resId = R.raw.bomb_blast_sound,
        volume = 1f,
        maxMillis = 3000L
    )

    // ----- Game over -----
    val gameOver = SoundEffect(
        resId = R.raw.game_over_sound,
        volume = 1.2f,
        maxMillis = 0L           // full length
    )

    val gameOverNewHighScore = SoundEffect(
        resId = R.raw.game_over_high_score,
        volume = 1f,
        maxMillis = 0L           // full length
    )

    // ----- High score during a game -----
    val highScoreConfetti = SoundEffect(
        resId = R.raw.high_score_conffeti_sound,
        volume = 1f,
        maxMillis = 3000L
    )

    // ----- No file yet. When you add one, set its resId, e.g. R.raw.life_lost -----
    val lifeLost = SoundEffect(resId = null)
    val levelUp = SoundEffect(resId = null)
    val uiTouch = SoundEffect(
        resId = R.raw.ui_touch,   // keep your existing resId
        volume = 0.7f,
        maxMillis = 600L,
        cooldownMillis = 120L,
        pausable = false
    )

    val restart = SoundEffect(
        resId = R.raw.restart_sound,    // keep your existing resId (use your file name)
        volume = 10000000000000000000f,
        pausable = false
    )
    val handRestartProgress = SoundEffect(
        resId = R.raw.progress_sound,
        volume = 0.5f,
        maxMillis = 800L,
        cooldownMillis = 300L
    )




    // Every effect above. GameSounds loads each file once from this list.
    val allEffects: List<SoundEffect>
        get() = listOf(
            fruitCut,
            smallBombBlast,
            bigBombBlast,
            gameOver,
            gameOverNewHighScore,
            highScoreConfetti,
            lifeLost,
            levelUp,
            restart,
            uiTouch,
            handRestartProgress

        )
}


//
//private val music: MediaPlayer? =
//    MediaPlayer.create(context, R.raw.game_background_loop)?.apply {
//        isLooping = true            // start again when the song ends
//        setVolume(0.35f, 0.35f)     // quiet, so effects are heard on top
//    }