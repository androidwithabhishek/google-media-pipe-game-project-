package gupta.abhishek.googlemediapipe.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper

// ---------------------------------------------------------------------
// GameSounds
//   Plays the game's sound effects and background music.
//   Settings (files, volume, length, cooldown) come from GameSoundSettings.kt.
// ---------------------------------------------------------------------
class GameSounds(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(GameSoundSettings.MAX_SIMULTANEOUS_SOUNDS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Each file is loaded once: file resource id -> SoundPool id.
    private val loadedIds = HashMap<Int, Int>()

    // When each file last started (milliseconds), for the cooldown.
    private val lastStartedAt = HashMap<Int, Long>()

    // Used to stop a sound after its maxMillis.
    private val handler = Handler(Looper.getMainLooper())

    private val music: MediaPlayer? = GameSoundSettings.backgroundMusicResId?.let { resId ->
        MediaPlayer.create(context, resId)?.apply {
            isLooping = true
            setVolume(GameSoundSettings.MUSIC_VOLUME, GameSoundSettings.MUSIC_VOLUME)
        }
    }



    init {
        for (effect in GameSoundSettings.allEffects) {
            val resId = effect.resId ?: continue
            if (!loadedIds.containsKey(resId)) {
                loadedIds[resId] = soundPool.load(context, resId, 1)
            }
        }
    }

    // Starts one sound effect, applying its volume, cooldown and maximum length.
    private fun play(effect: SoundEffect) {
        val resId = effect.resId ?: return          // no file yet: stay silent
        val soundId = loadedIds[resId] ?: return

        val now = System.currentTimeMillis()
        val last = lastStartedAt[resId] ?: 0L
        if (now - last < effect.cooldownMillis) return

        val streamId = soundPool.play(soundId, effect.volume, effect.volume, 1, 0, 1f)
        if (streamId == 0) return                   // 0 = the sound could not start

        lastStartedAt[resId] = now
        if (effect.maxMillis > 0L) {
            handler.postDelayed({ soundPool.stop(streamId) }, effect.maxMillis)
        }
    }

    // ---------------- PLAY FUNCTIONS ----------------

    fun playFruitCut() = play(GameSoundSettings.fruitCut)

    fun playSmallBombBlast() = play(GameSoundSettings.smallBombBlast)

    fun playBigBombBlast() = play(GameSoundSettings.bigBombBlast)

    // newHighScore = true plays the happy game over sound.
    fun playGameOver(newHighScore: Boolean = false) =
        play(if (newHighScore) GameSoundSettings.gameOverNewHighScore else GameSoundSettings.gameOver)

    fun playHighScoreConfetti() = play(GameSoundSettings.highScoreConfetti)

    fun playLifeLost() = play(GameSoundSettings.lifeLost)

    fun playLevelUp() = play(GameSoundSettings.levelUp)

    fun playRestart() = play(GameSoundSettings.restart)

    fun playUiTouch() = play(GameSoundSettings.uiTouch)


    fun handRestartProgressSound() = play(GameSoundSettings.handRestartProgress)

   

    fun startMusic() {
        if (music != null && !music.isPlaying) music.start()
    }

    fun pauseMusic() {
        if (music != null && music.isPlaying) music.pause()
    }

    // Frees the sounds. MainScreen calls this when the screen closes.
    fun release() {
        handler.removeCallbacksAndMessages(null)
        soundPool.release()
        music?.release()
    }
}