package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.GeneralSecurityException
import javax.crypto.AEADBadTagException
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val scope = rememberCoroutineScope()
            Box(
                Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()
            ) {
                Column(
                    Modifier.align(Alignment.Center),
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                callFoo().exceptionOrNull()?.printStackTrace()
                            }
                        },
                    ) {
                        Text(text = "Foo")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                callBar().exceptionOrNull()?.printStackTrace()
                            }
                        },
                    ) {
                        Text(text = "Bar")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                runCatching { callDecrypt() }.exceptionOrNull()?.printStackTrace()
                            }
                        },
                    ) {
                        Text(text = "decrypt")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                runCatching { suspendA() }.exceptionOrNull()?.printStackTrace()
                                suspendA()
                            }
                        },
                    ) {
                        Text(text = "suspendA")
                    }
                }
            }
        }
    }
}

suspend fun callFoo(): Result<Unit> {
    return runCatching {
        foo(Dispatchers.Default)
    }
}

suspend fun foo(coroutineContext: CoroutineContext) {
    try {
        withContext(coroutineContext) {
            throw Exception("nested crash")
        }
    } catch (t: Throwable) {
        throw Exception("foo failed", t)
    }
}

suspend fun callBar(): Result<Nothing> {
    return bar(Dispatchers.Default)
}

suspend fun bar(coroutineContext: CoroutineContext): Result<Nothing> {
    return runCatching {
        withContext(coroutineContext) {
            throw Exception("nested crash")
        }
    }
}

suspend fun callDecrypt() {
    withContextCatching {
        decrypt(Dispatchers.Default)
    }
}

suspend fun decrypt(coroutineContext: CoroutineContext) {
    withContext(coroutineContext) {
        throw AEADBadTagException("crypto crash")
    }
}

/**
 * Re-throw generic [GeneralSecurityException] to keep stack
 */
private suspend fun <T> withContextCatching(block: suspend CoroutineScope.() -> T): T = try {
    withContext(Dispatchers.Default, block)
} catch (e: GeneralSecurityException) {
    throw GeneralSecurityException(e)
}

suspend fun suspendA(): String {
    val b = suspendB()
    return b.getOrNull() ?: throw Exception("suspendA failed", b.exceptionOrNull())
}

suspend fun suspendB(): Result<String> {
    return suspendC()
}

suspend fun suspendC(): Result<String> {
    return suspendD()
}

suspend fun suspendD(): Result<String> {
    return runCatching {
        withContext(Dispatchers.Default) {
            throw Exception("from D")
        }
    }
}