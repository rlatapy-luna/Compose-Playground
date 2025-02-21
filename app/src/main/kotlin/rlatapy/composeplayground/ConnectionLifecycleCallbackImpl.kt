package rlatapy.composeplayground

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.Payload
import java.io.File
import java.io.FileNotFoundException
import java.time.Instant

class ConnectionLifecycleCallbackImpl(
    private val context: Context,
    private val id: String,
    private val onFileReceived: (File) -> Unit,
) : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
        Log.d("nearby", "[$id] onConnectionInitiated($endpointId, $connectionInfo)")

        AlertDialog.Builder(context)
            .setTitle("Accept connection to " + connectionInfo.endpointName)
            .setMessage("Confirm the code matches on both devices: " + connectionInfo.authenticationDigits)
            .setPositiveButton(
                "Accept"
            ) { dialog: DialogInterface?, which: Int ->  // The user confirmed, so we can accept the connection.
                Log.d("nearby", "[$id] Accept connection")
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, PayloadCallbackImpl(context, id, onFileReceived))
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialog: DialogInterface?, which: Int ->  // The user canceled, so we should reject the connection.
                Nearby.getConnectionsClient(context).rejectConnection(endpointId)
            }
            .setIcon(R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onConnectionResult(endpointId: String, connectionResolution: ConnectionResolution) {
        Log.d("nearby", "[$id] onConnectionResult($endpointId, ${connectionResolution.status.statusMessage})")
        if (connectionResolution.status.isSuccess) {
            sendHello(endpointId)
        }
    }

    private fun sendHello(endpointId: String) {
        val fileToSend = File(context.cacheDir, "hello_advertiser")
        fileToSend.delete()
        fileToSend.writeText("Hello from $id (${deviceName()}) at ${Instant.now()}")
        try {
            val filePayload: Payload = Payload.fromFile(fileToSend)
            Nearby.getConnectionsClient(context).sendPayload(endpointId, filePayload)
        } catch (e: FileNotFoundException) {
            Log.e("MyApp", "File not found", e)
        }
    }

    override fun onDisconnected(endpointId: String) {
        Log.d("nearby", "[$id] onDisconnected($endpointId)")
    }
}
