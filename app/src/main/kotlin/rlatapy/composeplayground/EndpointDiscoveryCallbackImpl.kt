package rlatapy.composeplayground

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import java.io.File

class EndpointDiscoveryCallbackImpl(private val context: Context, private val onFileReceived: (File) -> Unit) : EndpointDiscoveryCallback() {
    override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
        Log.d("nearby", "onEndpointFound($endpointId, $discoveredEndpointInfo)")
        Nearby.getConnectionsClient(context)
            .requestConnection(deviceName(), endpointId, ConnectionLifecycleCallbackImpl(context, "discoverer", onFileReceived))
            .addOnSuccessListener { result ->
                Log.d("nearby", "requestConnection onSuccess $result")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("nearby", "requestConnection onFailure", e)
            }
    }

    override fun onEndpointLost(endpointId: String) {
        Log.d("nearby", "onEndpointLost($endpointId)")
    }
}