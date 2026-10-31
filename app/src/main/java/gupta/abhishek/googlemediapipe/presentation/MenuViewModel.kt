package gupta.abhishek.googlemediapipe.presentation



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gupta.abhishek.googlemediapipe.core.session.GameSession
import gupta.abhishek.googlemediapipe.domain.model.PlayerSettings
import gupta.abhishek.googlemediapipe.domain.repository.ScoreRepository
import gupta.abhishek.googlemediapipe.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MenuViewModel @Inject constructor(
    scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository,
    session: GameSession
) : ViewModel() {

    // High score shown on the menu.
    val bestScore: StateFlow<Int> = scoreRepository.observeBestScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Sound and music settings.
    val settings: StateFlow<PlayerSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerSettings())

    // True when a game is running or was left unfinished: the button says "Continue".
    val gameInProgress: StateFlow<Boolean> = session.gameInProgress

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setMusicEnabled(enabled) }
    }
}