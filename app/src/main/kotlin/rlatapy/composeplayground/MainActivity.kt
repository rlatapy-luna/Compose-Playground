package rlatapy.composeplayground

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy

private const val SERVICE_ID = "rlatapy.composeplayground"
private val STRATEGY = Strategy.P2P_POINT_TO_POINT

class MainActivity : ComponentActivity() {

    private val info = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
            .launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES,
                )
            )

        setContent {
            Column(
                Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(onClick = { startAdvertising() }) {
                    Text("Start Advertising")
                }
                Button(onClick = { startDiscovery() }) {
                    Text("Start Discovery")
                }
                Text(info.value)
            }
        }
    }

    private fun startAdvertising() {
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        val connectionLifecycleCallback = ConnectionLifecycleCallbackImpl(this, "advertiser") {
            info.value = it.readText()
        }
        Nearby.getConnectionsClient(this)
            .startAdvertising(
                deviceName(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { result ->
                Log.d("nearby", "startAdvertising onSuccess $result")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("nearby", "startAdvertising onFailure", e)
            }
    }

    private fun startDiscovery() {
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        val endpointDiscoveryCallback = EndpointDiscoveryCallbackImpl(this) {
            info.value = it.readText()
        }
        Nearby.getConnectionsClient(this)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { result ->
                Log.d("nearby", "startDiscovery onSuccess $result")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("nearby", "startDiscovery onFailure", e)
            }
    }
}
