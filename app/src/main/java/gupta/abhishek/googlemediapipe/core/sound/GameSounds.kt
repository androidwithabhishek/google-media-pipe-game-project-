package gupta.abhishek.googlemediapipe.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import gupta.abhishek.googlemediapipe.R


// ---------------------------------------------------------------------
// GameSounds
//   Loads all sound files from res/raw and plays them.
//
//   Short effects (pop, swoosh, ...)  -> SoundPool  (fast, can overlap)
//   Long background music             -> MediaPlayer (loops forever)
//
//   Usage:
//     val sounds = GameSounds(context)
//     sounds.playCatch()
//     sounds.startMusic()
//     sounds.release()      <- call when the screen closes
// ---------------------------------------------------------------------
class GameSounds(context: Context) {

    // ---------------- SHORT EFFECTS ----------------

    // Tell Android these sounds belong to a game.
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    // SoundPool = the box that holds the short effects. Up to 6 can play at once.
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(audioAttributes)
        .build()

    // Load every effect once. load() gives back a number we use later to play it.
    private val swoosh       = soundPool.load(context, R.raw.playful_swoosh, 1)
    private val catchPop     = soundPool.load(context, R.raw.fruit_catch_pop, 1)
    private val plusFour     = soundPool.load(context, R.raw.score_plus_four, 1)
    private val lifeLost     = soundPool.load(context, R.raw.soft_life_lost, 1)
    private val gameOver     = soundPool.load(context, R.raw.game_over, 1)
    private val levelUp      = soundPool.load(context, R.raw.bonus_level_up, 1)
    private val whoosh       = soundPool.load(context, R.raw.falling_whoosh, 1)
    private val restart      = soundPool.load(context, R.raw.restart, 1)
    private val highScore    = soundPool.load(context, R.raw.high_score_fanfare, 1)
    private val uiTouch      = soundPool.load(context, R.raw.ui_touch, 1)


    // ---------------- BACKGROUND MUSIC ----------------

    // create() can return null if the file is broken, so the "?" is needed.
    private val music: MediaPlayer? =
        MediaPlayer.create(context, R.raw.game_background_loop)?.apply {
            isLooping = true            // start again when the song ends
            setVolume(0.35f, 0.35f)     // quiet, so effects are heard on top
        }


    // ---------------- PLAY FUNCTIONS ----------------

    // The dagger swings through the fruit.
    fun playSwoosh() = play(swoosh, 0.5f)

    // Ball was caught.
    fun playCatch() = play(catchPop)

    // Bonus ping for every 4th point.
    fun playScorePlusFour() = play(plusFour)

    // Bonus fanfare for every 10th point (the ball gets faster).
    fun playLevelUp() = play(levelUp)

    // Ball fell off the bottom.
    fun playFall() = play(whoosh, 0.7f)

    // You lost one life.
    fun playLifeLost() = play(lifeLost)

    // No lives left.
    fun playGameOver() = play(gameOver)

    // Game was restarted.
    fun playRestart() = play(restart)

    // You beat the high score.
    fun playHighScore() = play(highScore)

    // A button was touched.
    fun playUiTouch() = play(uiTouch, 0.8f)

    // Plays one effect. volume goes from 0.0 (silent) to 1.0 (full).
    private fun play(soundId: Int, volume: Float = 1f) {
        // play(id, leftVolume, rightVolume, priority, loop (0 = no), speed)
        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    // Start (or continue) the background music.
    fun startMusic() {
        if (music != null && !music.isPlaying) {
            music.start()
        }
    }

    // Pause the background music (it continues from the same spot later).
    fun pauseMusic() {
        if (music != null && music.isPlaying) {
            music.pause()
        }
    }

    // Free the memory. Call this when the game screen closes.
    fun release() {
        soundPool.release()
        music?.release()
    }
}