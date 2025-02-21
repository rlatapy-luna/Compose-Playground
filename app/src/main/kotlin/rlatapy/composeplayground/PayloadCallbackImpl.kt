package rlatapy.composeplayground

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import java.io.File
import java.io.IOException

class PayloadCallbackImpl(
    private val context: Context,
    private val id: String,
    private val onFileReceived: (File) -> Unit,
) : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        Log.d("nearby", "[$id] onPayloadReceived($endpointId, $payload)")

        payload.asFile()?.asUri()?.let { uri ->
            try {
                val file = File(context.cacheDir, "received")
                context.contentResolver.openInputStream(uri).use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output) ?: Log.e("nearby", "[$id] onPayloadReceived unable to open uri $uri")
                    }
                }
                onFileReceived(file)
                Log.d("nearby", "[$id] onPayloadReceived file received\n\t${file.readText()}")
            } catch (e: IOException) {
                Log.e("nearby", "[$id] onPayloadReceived io error", e)
            } finally {
                context.contentResolver.delete(uri, null, null)
            }
        } ?: Log.e("nearby", "[$id] onPayloadReceived payload is not a file $payload")
    }

    override fun onPayloadTransferUpdate(endpointId: String, payloadTransferUpdate: PayloadTransferUpdate) {
        Log.d("nearby", "[$id] onPayloadTransferUpdate($endpointId, $payloadTransferUpdate)")
    }
}