package rlatapy.composeplayground

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.seconds

internal object RemoteUtils {
    @OptIn(ExperimentalSerializationApi::class)
    internal fun HttpClientConfig<*>.configureJson() {
        // Configure Json serialization.
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // ignore key not handled.
                    explicitNulls = false // do no set null by default on missing key.
                },
            )
        }
    }

    internal fun HttpClientConfig<*>.configureLogger() {
        install(plugin = Logging) {
            level = LogLevel.ALL

            this.logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }

    internal fun HttpClientConfig<*>.configureTimeout() {
        install(HttpTimeout) {
            requestTimeoutMillis = 30.seconds.inWholeMilliseconds
            connectTimeoutMillis = 30.seconds.inWholeMilliseconds
        }
    }

    fun buildAndroidHttpClient(
        block: HttpClientConfig<*>.() -> Unit,
    ): HttpClient {
        return HttpClient(engine = Android.create(), block = block)
    }

    fun AndroidEngineConfig.configureSslManager() {
        sslManager = { httpsURLConnection ->
            httpsURLConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
            httpsURLConnection.sslSocketFactory = SSLContext.getInstance("TLS")
                .apply { init(null, arrayOf(AllCertsTrustManager()), SecureRandom()) }.socketFactory
        }
    }

    @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
    class AllCertsTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}
