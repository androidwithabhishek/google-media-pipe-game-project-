package gupta.abhishek.googlemediapipe.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

// ---------------------------------------------------------------------
// GameSounds
//   Plays sound effects and music. Settings come from GameSoundSettings.kt.
//   When the game is paused, or the app goes to the background:
//     - game sounds and music pause
//     - button sounds (pausable = false) keep playing
// ---------------------------------------------------------------------
class GameSounds(context: Context) {

    // A game sound that is playing: its SoundPool stream and when it must stop.
    private class ActiveSound(val streamId: Int, var stopAt: Long)

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

    // Game sounds that are playing, oldest first.
    private val activeSounds = LinkedHashMap<Int, ActiveSound>()

    private val handler = Handler(Looper.getMainLooper())

    // Why game sounds may be paused: the game is paused, or the app is in the background.
    private var gamePaused = false
    private var appInForeground = true
    private var effectsPaused = false
    private var pausedAt = 0L

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

    // Checks every few milliseconds and stops the timed sounds whose time is up.
    private val stopChecker = Runnable { checkStops() }

    private fun checkStops() {
        val now = SystemClock.elapsedRealtime()
        val iterator = activeSounds.values.iterator()
        while (iterator.hasNext()) {
            val sound = iterator.next()
            if (sound.stopAt <= now) {
                soundPool.stop(sound.streamId)
                iterator.remove()
            }
        }
        // Keep checking while a timed sound is still playing.
        if (!effectsPaused && activeSounds.values.any { it.stopAt != Long.MAX_VALUE }) {
            handler.postDelayed(stopChecker, STOP_CHECK_MILLIS)
        }
    }

    // Starts one sound effect, applying its volume, cooldown, maximum length and pause rules.
    private fun play(effect: SoundEffect) {
        val resId = effect.resId ?: return          // no file yet: stay silent
        val soundId = loadedIds[resId] ?: return

        // No new game sounds while paused or in the background.
        if (effectsPaused && effect.pausable) return

        val now = SystemClock.elapsedRealtime()
        val last = lastStartedAt[resId] ?: 0L
        if (now - last < effect.cooldownMillis) return

        val streamId = soundPool.play(soundId, effect.volume, effect.volume, 1, 0, 1f)
        if (streamId == 0) return                   // 0 = the sound could not start

        lastStartedAt[resId] = now

        if (effect.pausable) {
            val stopAt = if (effect.maxMillis > 0L) now + effect.maxMillis else Long.MAX_VALUE
            activeSounds[streamId] = ActiveSound(streamId, stopAt)
            if (activeSounds.size > MAX_TRACKED_SOUNDS) {
                activeSounds.remove(activeSounds.keys.first())
            }
            handler.removeCallbacks(stopChecker)
            handler.post(stopChecker)
        } else if (effect.maxMillis > 0L) {
            // Button sounds are not paused, so a simple timer is enough.
            handler.postDelayed({ soundPool.stop(streamId) }, effect.maxMillis)
        }
    }

    // Pauses or resumes game sounds and music, based on the two pause reasons.
    private fun applyPauseState() {
        val shouldPause = gamePaused || !appInForeground

        if (shouldPause && !effectsPaused) {
            effectsPaused = true
            pausedAt = SystemClock.elapsedRealtime()
            handler.removeCallbacks(stopChecker)
            for (sound in activeSounds.values) {
                soundPool.pause(sound.streamId)
            }
        } else if (!shouldPause && effectsPaused) {
            effectsPaused = false
            // Time spent paused does not count toward a sound's maximum length.
            val pausedFor = SystemClock.elapsedRealtime() - pausedAt
            for (sound in activeSounds.values) {
                soundPool.resume(sound.streamId)
                if (sound.stopAt != Long.MAX_VALUE) sound.stopAt += pausedFor
            }
            handler.removeCallbacks(stopChecker)
            handler.post(stopChecker)
        }

        val player = music ?: return
        if (shouldPause) {
            if (player.isPlaying) player.pause()
        } else if (!player.isPlaying) {
            player.start()
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

    // Plays when a hand starts holding the replay button.
    fun handRestartProgressSound() = play(GameSoundSettings.handRestartProgress)

    // ---------------- PAUSE AND MUSIC ----------------

    // CameraPreview calls this when the game is paused or continued.
    fun setGamePaused(paused: Boolean) {
        gamePaused = paused
        applyPauseState()
    }

    // MainScreen calls this when the app comes to the front (and at start).
    fun startMusic() {
        appInForeground = true
        applyPauseState()
    }

    // MainScreen calls this when the app goes to the background.
    fun pauseMusic() {
        appInForeground = false
        applyPauseState()
    }

    // Frees the sounds. MainScreen calls this when the screen closes.
    fun release() {
        handler.removeCallbacksAndMessages(null)
        activeSounds.clear()
        soundPool.release()
        music?.release()
    }

    private companion object {
        const val STOP_CHECK_MILLIS = 50L
        const val MAX_TRACKED_SOUNDS = 16
    }
}