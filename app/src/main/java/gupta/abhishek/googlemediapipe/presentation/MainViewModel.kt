package gupta.abhishek.googlemediapipe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gupta.abhishek.googlemediapipe.domain.repository.ScoreRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    // Best score from the database. Starts at 0 and updates as soon as the database answers.
    val bestScore: StateFlow<Int> = scoreRepository.observeBestScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Called when a game ends. A score of 0 is not worth saving.
    fun saveScore(score: Int) {
        if (score <= 0) return
        viewModelScope.launch { scoreRepository.saveScore(score) }
    }
}
