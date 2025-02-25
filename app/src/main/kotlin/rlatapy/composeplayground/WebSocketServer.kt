package rlatapy.composeplayground

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketServer : WebSocketListener() {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun startServer() {
        val request = Request.Builder().url("ws://192.168.1.45:8080").build()
        webSocket = client.newWebSocket(request, this)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // Handle SDP & ICE candidates here
    }
}