package rlatapy.composeplayground

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyClass()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = displayManager.displays

        Log.d("displays", displays.joinToString("\n"))

        val options = ActivityOptions.makeBasic()
        options.setLaunchDisplayId(displays[1].displayId)
        val secondaryIntent = Intent(this, SecondaryActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(secondaryIntent, options.toBundle())

        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
            )
        }
    }
}
