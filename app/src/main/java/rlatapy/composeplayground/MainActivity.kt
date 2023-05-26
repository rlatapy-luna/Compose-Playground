package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()

            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                isNavigationBarContrastEnforced = false,
                darkIcons = !isSystemInDarkTheme(),
            )

            MaterialTheme {
                Column(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    var toggle by remember {
                        mutableStateOf(true)
                    }

                    Button(onClick = {
                        toggle = !toggle
                    }) {
                        Text(text = "toggle")
                    }
                    LazyColumn {
                        if (toggle) {
                            item {
                                Text(text = "text 1", Modifier.fillMaxWidth())
                            }
                        } else {
                            item(key = 2) {
                                Text(text = "text 2", Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }
}
