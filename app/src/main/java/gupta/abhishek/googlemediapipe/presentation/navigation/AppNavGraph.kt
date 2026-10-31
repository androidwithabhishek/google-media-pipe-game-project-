package gupta.abhishek.googlemediapipe.presentation.navigation



import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import gupta.abhishek.googlemediapipe.core.google_media_pipe.HandLandmarkerHelper
import gupta.abhishek.googlemediapipe.presentation.screens.game_screens.MainScreen
import gupta.abhishek.googlemediapipe.presentation.screens.other_screens.MainMenuScreen
import gupta.abhishek.googlemediapipe.presentation.screens.other_screens.test.FruitTestScreen

// The whole navigation of the app: menu, game and fruit test.
@Composable
fun AppNavGraph(
    helper: HandLandmarkerHelper,
    handResult: HandLandmarkerResult?,
    showHandAnimation: Boolean,
    onCameraNeeded: () -> Unit
) {
    val navController = rememberNavController()

    // Opens a fresh game. Any old game still on the back stack is removed first,
    // so the new game starts at score 0 with full lives.
    val startNewGame: () -> Unit = {
        navController.navigate(GameRoute) {
            popUpTo<GameRoute> { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = MenuRoute
    ) {

        composable<MenuRoute> {
            MainMenuScreen(
                onPlayClick = startNewGame,
                // The menu only shows "Continue" when a game is running, and that game is
                // still on the back stack, so popping returns to it with its score and lives.
                onContinueClick = { navController.popBackStack() },
                onFruitTestClick = { navController.navigate(FruitTestRoute) }
            )
        }


        composable<GameRoute> {
            // Back from the game opens the menu. The game stays on the back stack
            // underneath, so "Continue" returns to it.
            BackHandler { navController.navigate(MenuRoute) }

            MainScreen(
                helper = helper,
                handResult = handResult,
                showHandAnimation = showHandAnimation,
                onCameraNeeded = onCameraNeeded,
                onMenuClick = { navController.navigate(MenuRoute) }
            )
        }

        composable<FruitTestRoute> {
            FruitTestScreen(onGoToGame = startNewGame)
        }
    }
}