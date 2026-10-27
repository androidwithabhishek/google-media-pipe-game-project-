package gupta.abhishek.googlemediapipe.presentation.screen.game_logic

// ---------------------------------------------------------------------
// GAME SETTINGS
// Change these numbers to tune the game. Sizes in dp, text in sp, distances in pixels.
// ---------------------------------------------------------------------

// ----- Fruit size -----
internal const val FRUIT_BASE_RADIUS_PX = 120f     // base size of every fruit, in pixels
internal const val FRUIT_SIZE_MULTIPLIER = 1.2f    // global size: 1.2f = 20% bigger, 0.8f = 20% smaller

// ----- Bombs -----
internal const val BOMB_SPAWN_CHANCE = 0.18f       // chance the next item is a bomb (0.18 = 18%)
internal const val BOMBS_UNLOCK_SCORE = 3          // no bombs until the score reaches this
internal const val SMALL_BOMB_LIVES_LOST = 1       // lives lost when you cut the small bomb
internal const val SMALL_BOMB_SCORE_PENALTY = 3    // points taken away when you cut the small bomb

// ----- Camera shake on cuts -----
internal const val CUT_SHAKE_STRENGTH_PX = 10f     // how far the picture jolts on every cut
internal const val CUT_SHAKE_DURATION_SECONDS = 0.15f

// ----- Movement -----
internal const val FRUIT_SPIN_DEGREES_PER_SECOND = 90f
internal const val START_FALL_SPEED_PX_PER_SECOND = 400f
internal const val FALL_SPEED_INCREASE_PER_POINT = 40f
internal const val MAX_FALL_SPEED_PX_PER_SECOND = 1600f

// ----- Cutting -----
internal const val CATCH_EXTRA_REACH_PX = 20f      // hand: extra distance so catching feels easier
internal const val TOUCH_CUT_REACH_PX = 30f        // finger: how close a swipe must pass to cut (bigger = easier)

// ----- Lives -----


// ----- Restart button (round, top right, shown on game over). Sizes in dp. -----
internal const val RESTART_BUTTON_SIZE_DP = 170f
internal const val RESTART_BUTTON_MARGIN_TOP_DP = 50f
internal const val RESTART_BUTTON_MARGIN_END_DP = 50f
internal const val RESTART_HOLD_SECONDS = 0.5f     // keep your hand on the button this long (0 = instant)

// ----- HUD (score, hearts, buttons). Sizes in dp, text sizes in sp. -----
internal const val SHOW_HAND_HINT = false          // true = show "Show your hand" when no hand is seen
internal const val HUD_MARGIN_DP = 16f             // gap between the HUD and the screen edges
internal const val SCORE_ICON_SIZE_DP = 76f        // size of the half apple next to the score
internal const val SCORE_TEXT_SIZE_SP = 64f        // size of the score number
internal const val BEST_TEXT_SIZE_SP = 36f         // size of the "Best" line
internal const val HEART_SIZE_DP = 35f             // size of one heart (one heart per life)


// ----- Bottom-left buttons: hand button sits on top of the pause button -----
internal const val PAUSE_BUTTON_SIZE_DP = 56f      // pause button size
internal const val PAUSE_BUTTON_MARGIN_DP = 16f    // gap between the buttons and the screen edges
internal const val HAND_BUTTON_SIZE_DP = 56f       // hand on/off button size
internal const val HAND_BUTTON_GAP_DP = 12f        // gap between the hand button and the pause button

// ----- Confetti (shown when you beat the high score) -----
internal const val CONFETTI_PIECE_COUNT = 140      // how many paper pieces
internal const val CONFETTI_SECONDS = 3f           // how long the confetti falls
internal const val CONFETTI_GRAVITY = 1.2f         // how fast the pieces fall (bigger = faster)


internal const val SHOW_HEART_BACKGROUND = false    // false = hearts without the dark box behind them

internal const val STARTING_LIVES = 5

internal const val SCORE_ICON_RATIO = 1.3f    // apple size as a share of the score text size

internal const val SCORE_TEXT_OFFSET_Y_DP = 6f    // how far the score moves down (try 4f to 10f)


