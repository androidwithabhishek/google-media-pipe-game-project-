package gupta.abhishek.googlemediapipe.presentation.screen.game_logic



// ---------------------------------------------------------------------
// GAME SETTINGS
// Change these numbers to tune the game.
// ---------------------------------------------------------------------

// ----- Fruit size -----
internal const val FRUIT_BASE_RADIUS_PX = 120f     // base size of every fruit, in pixels
internal const val FRUIT_SIZE_MULTIPLIER = 1.2f    // global size: 1.2f = 20% bigger, 0.8f = 20% smaller

// ----- Bombs -----
internal const val BOMB_SPAWN_CHANCE = 0.18f       // chance the next item is a bomb (0.18 = 18%)
internal const val BOMBS_UNLOCK_SCORE = 3          // no bombs until the score reaches this

// ----- Camera shake on cuts -----
internal const val CUT_SHAKE_STRENGTH_PX = 10f     // how far the picture jolts on every cut
internal const val CUT_SHAKE_DURATION_SECONDS = 0.15f

// ----- Movement -----
internal const val FRUIT_SPIN_DEGREES_PER_SECOND = 90f
internal const val START_FALL_SPEED_PX_PER_SECOND = 400f
internal const val FALL_SPEED_INCREASE_PER_POINT = 40f
internal const val MAX_FALL_SPEED_PX_PER_SECOND = 1600f

// ----- Hand catching -----
internal const val CATCH_EXTRA_REACH_PX = 20f      // extra distance so catching feels easier

// ----- Lives -----
internal const val STARTING_LIVES = 5

// ----- Restart button (round, top right, shown on game over). Sizes in dp. -----
internal const val RESTART_BUTTON_SIZE_DP = 170f
internal const val RESTART_BUTTON_MARGIN_TOP_DP = 50f
internal const val RESTART_BUTTON_MARGIN_END_DP = 50f
internal const val RESTART_HOLD_SECONDS = 0.5f     // keep your hand on the button this long (0 = instant)

// ----- HUD (score, hearts, pause button). Sizes in dp, text sizes in sp. -----
internal const val SHOW_HAND_HINT = false           // true = show "Show your hand" when no hand is seen, false = never show it
internal const val HUD_MARGIN_DP = 16f             // gap between the HUD and the screen edges
internal const val SCORE_ICON_SIZE_DP = 76f        // size of the half apple next to the score
internal const val SCORE_TEXT_SIZE_SP = 64f        // size of the score number
internal const val BEST_TEXT_SIZE_SP = 36f         // size of the "Best" line
internal const val HEART_SIZE_DP = 40f             // size of one heart (one heart per life)
internal const val PAUSE_BUTTON_SIZE_DP = 56f      // small pause button, bottom left
internal const val PAUSE_BUTTON_MARGIN_DP = 16f    // gap between the pause button and the screen edges