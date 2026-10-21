package gupta.abhishek.googlemediapipe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Starts Hilt for the whole app. Must be listed in AndroidManifest.xml as android:name=".GameApp".
@HiltAndroidApp
class GameApp : Application()
