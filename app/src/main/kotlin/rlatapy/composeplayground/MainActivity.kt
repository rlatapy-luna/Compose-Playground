package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box(
                Modifier
                    .safeDrawingPadding()
                    .padding(16.dp)
            ) {
                var screen: Int by remember { mutableIntStateOf(1) }
                when (screen) {
                    1 -> Screen1 { screen = 2 }
                    2 -> Screen2 { screen = 3 }
                    else -> Screen3 { screen = 1 }
                }
            }
        }
    }
}

@Composable
fun Screen1(
    nav2: () -> Unit,
) {
    Column {
        Card {
            Text(text = "Screen 1")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = LoremIpsum(50).values.joinToString())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card {
            Text(text = "Screen 1")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = LoremIpsum(50).values.joinToString())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = nav2) {
            Text("Hello")
        }
    }
}

@Composable
fun Screen2(
    nav3: () -> Unit,
) {
    Column {
        Card {
            Text(text = "Screen 2")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = LoremIpsum(50).values.joinToString())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = nav3) {
            Text("Hello")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card {
            Text(text = "Screen 2")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = LoremIpsum(50).values.joinToString())
        }
    }
}

@Composable
fun Screen3(
    nav1: () -> Unit,
) {
    Column {
        Card {
            Text(text = "Screen 3")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = LoremIpsum(50).values.joinToString())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = nav1) {
            Text("Not hello")
        }
    }
}

@Preview
@Composable
fun ScreenPreview() {
    Screen1 {}
}