package rlatapy.composeplayground

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.webrtc.Camera2Enumerator
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack


val receivedOfferSdp = "SDP_OFFER_FROM_SENDER"

class MainActivity : ComponentActivity() {

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var videoCapturer: VideoCapturer
    private lateinit var localView: SurfaceViewRenderer
    private lateinit var remoteView: SurfaceViewRenderer

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
            .launch(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                )
            )

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

        localView = SurfaceViewRenderer(this)
        remoteView = SurfaceViewRenderer(this)
        val eglBaseContext = EglBase.create().eglBaseContext
        localView.init(eglBaseContext, null)
        remoteView.init(eglBaseContext, null)

        videoCapturer = createCameraCapturer()
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(SurfaceTextureHelper.create("CaptureThread", eglBaseContext), this, videoSource.capturerObserver)

        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
        localVideoTrack.addSink(localView)

        setContent {
            Column(
                Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        setPeerConnection()
                    }
                ) {
                    Text("createPeerConnection")
                }
                Button(
                    onClick = {
                        createOffer()
                    }
                ) {
                    Text("createOffer")
                }
                Button(
                    onClick = {
                        WebSocketServer().startServer()
                    }
                ) {
                    Text("Sender server")
                }
                Button(
                    onClick = {
                        val client = OkHttpClient()
                        val request = Request.Builder().url("ws://192.168.1.45:8080").build()
                        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
                            override fun onMessage(webSocket: WebSocket, text: String) {
                                // Process received SDP or ICE candidates
                            }
                        })
                    }
                ) {
                    Text("Receiver listen")
                }
            }
        }
    }

    private fun createCameraCapturer(): VideoCapturer {
        val enumerator = Camera2Enumerator(this)
        for (deviceName in enumerator.deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) return videoCapturer
            }
        }
        throw RuntimeException("No back camera found")
    }

    private fun setPeerConnection() {
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, object : PeerConnection.Observer {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                // Send ICE candidate to the other device via a signaling method
            }

            override fun onAddStream(mediaStream: MediaStream) {
                runOnUiThread {
                    mediaStream.videoTracks[0].addSink(remoteView)
                }
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {}
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {}
            override fun onIceConnectionReceivingChange(p0: Boolean) {}
            override fun onSignalingChange(state: PeerConnection.SignalingState) {}
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
            override fun onRemoveStream(mediaStream: MediaStream) {}
            override fun onDataChannel(p0: DataChannel?) {}
            override fun onRenegotiationNeeded() {}
        })!!
    }

    private fun createOffer() {
        peerConnection.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription)
                // Send sessionDescription to the receiver via signaling
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String) {}
            override fun onSetFailure(error: String) {}
        }, MediaConstraints())
    }

    private fun setRemoteDescription() {
        peerConnection.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {}

            override fun onSetFailure(error: String) {}

            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription)
                // Send answer back to the sender via signaling
            }

            override fun onCreateFailure(error: String) {}
        }, SessionDescription(SessionDescription.Type.OFFER, receivedOfferSdp))
    }

    private fun initWebRtc() {

        // Initialize WebRTC
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions())

        // Create a PeerConnectionFactory
        val options = PeerConnectionFactory.Options()
        val peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).createPeerConnectionFactory()

        // Set up video capturing
        val rootEglBase = EglBase.create()
        val videoCapturer: VideoCapturer = createVideoCapturer()
        val videoSource: VideoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast())
        val localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
    }
}
