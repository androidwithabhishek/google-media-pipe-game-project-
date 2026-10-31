package gupta.abhishek.googlemediapipe.core.session



import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Remembers whether a game is running or was left unfinished. One copy for the whole app.
// The menu reads it to show "Continue" instead of "Play".
@Singleton
class GameSession @Inject constructor() {

    private val _gameInProgress = MutableStateFlow(false)
    val gameInProgress: StateFlow<Boolean> = _gameInProgress.asStateFlow()

    fun setGameInProgress(playing: Boolean) {
        _gameInProgress.value = playing
    }
}