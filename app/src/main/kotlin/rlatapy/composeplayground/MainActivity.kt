package rlatapy.composeplayground

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        // Don't use SystemBarStyle.auto for navigation bar because it always add a scrim (cf doc)
        val navigationBarStyle = if (isDark) {
            SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(scrim = android.graphics.Color.TRANSPARENT, darkScrim = android.graphics.Color.TRANSPARENT)
        }
        val statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        enableEdgeToEdge(
            statusBarStyle = statusBarStyle,
            navigationBarStyle = navigationBarStyle,
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var showBottomSheet by remember {
                mutableStateOf(false)
            }

            MaterialTheme {
                if (showBottomSheet) {
                    BottomSheetSample(closeBottomSheet = { showBottomSheet = false })
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button({ showBottomSheet = !showBottomSheet }) {
                        Text(text = "Toggle bottom sheet")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSample(
    closeBottomSheet: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var textValue2: String by remember { mutableStateOf("") }
    var textValue: String by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = closeBottomSheet,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(0),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Red)
        ) {
            TextField(
                value = textValue2, onValueChange = {},
                Modifier
                    .align(Alignment.TopCenter)
                    .systemBarsPadding()
            )
            Button({ closeBottomSheet() }, Modifier.align(Alignment.Center)) { Text(text = "close") }
            TextField(
                value = textValue, onValueChange = {},
                Modifier
                    .align(Alignment.BottomCenter)
                    .systemBarsPadding()
            )
        }
    }
}
