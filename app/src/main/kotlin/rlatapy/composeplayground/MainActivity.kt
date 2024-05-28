package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column {
                    Button(onClick = { viewModel.toggleShowCount() }) {
                        Text("Toggle count")
                    }
                    TextField(value = uiState.textCount.value, onValueChange = {
                        viewModel.setText(it)
                    })
                    Text(text = "count = ${uiState.textCount.count}")
                }
            }
        }
    }
}

class MainViewModel : ViewModel() {

    private val textFlow = MutableStateFlow("")
    private val showCountFlow = MutableStateFlow(false)

    private val textCountFlow = combine(
        this.textFlow,
        showCountFlow
    ) { text, showCount ->
        if (showCount) {
            TextCount(text, text.length)
        } else {
            TextCount(text, null)
        }
    }

    private suspend fun callSuspendFun() {
        val delay = Random.nextLong(10, 60)
        println("delay = $delay")
        delay(delay)
    }

    val uiState: StateFlow<UiState> = combine(
        textCountFlow,
        flowOf(Unit),
    ) { textCount, _ ->
        callSuspendFun()
        UiState(textCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UiState(TextCount("", null)))

    fun setText(value: String) {
        textFlow.value = value
    }

    fun toggleShowCount() {
        showCountFlow.value = !showCountFlow.value
    }
}

data class TextCount(
    val value: String,
    val count: Int?,
)

data class UiState(
    val textCount: TextCount,
)