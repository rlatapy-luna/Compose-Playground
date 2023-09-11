package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )

        super.onCreate(savedInstanceState)

        setContent {
            var showBottomSheet by remember {
                mutableStateOf(false)
            }

            var showBottomSheetScrollable by remember {
                mutableStateOf(false)
            }

            MaterialTheme {
                if (showBottomSheet) {
                    BottomSheetSampleBox(closeBottomSheet = { showBottomSheet = false })
                }
                if (showBottomSheetScrollable) {
                    BottomSheetSampleScrollableBox(closeBottomSheet = { showBottomSheetScrollable = false })
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Button(onClick = {
                            showBottomSheet = true
                        }) {
                            Text("Show bottom sheet")
                        }
                    }
                    item {
                        Button(onClick = {
                            showBottomSheetScrollable = true
                        }) {
                            Text("Show bottom sheet scrollable")
                        }
                    }
                    items(100) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(Random.nextInt()).copy(alpha = 1f))
                        )
                    }
                }
            }
        }
    }
}
