package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

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

            var showBottomSheet by remember {
                mutableStateOf(true)
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
                    Button(onClick = {
                        showBottomSheet = true
                    }) {
                        Text("Show bottom sheet")
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
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var textValue: String by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = closeBottomSheet,
        sheetState = bottomSheetState,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                // you must additionally handle intended state cleanup, if any.
                onClick = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            closeBottomSheet()
                        }
                    }
                }
            ) {
                Text("Hide Bottom Sheet")
            }
        }
        LazyColumn {
            item {
                TextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}