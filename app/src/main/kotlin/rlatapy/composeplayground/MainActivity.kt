package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val coroutineScope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                val cameraStreamRenderer = remember { CameraStreamRenderer() }
                cameraStreamRenderer.Composable(Modifier)

                Button(onClick = {
                    coroutineScope.launch {
                        val bitmap = cameraStreamRenderer.capture()
                        println(bitmap.width)
                    }
                }) {
                    Text("Capture")
                }
            }
        }
    }
}
