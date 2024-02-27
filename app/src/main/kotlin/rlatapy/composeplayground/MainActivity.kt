package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import rlatapy.composeplayground.RemoteUtils.buildAndroidHttpClient
import rlatapy.composeplayground.RemoteUtils.configureLogger
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

class MainActivity : ComponentActivity() {
    private val httpClient = buildAndroidHttpClient {
        configureLogger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

        }
    }
}
