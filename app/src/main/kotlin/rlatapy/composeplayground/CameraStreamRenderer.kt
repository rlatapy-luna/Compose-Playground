package rlatapy.composeplayground

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraStreamRenderer() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()

    suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase, imageCapture
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    @Composable
    fun Composable(modifier: Modifier) {
        //        val surfaceRequest by surfaceRequest.collectAsStateWithLifecycle()
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current

        LaunchedEffect(lifecycleOwner) {
            bindToCamera(context, lifecycleOwner)
        }

        //        surfaceRequest?.let { request ->
        //            CameraXViewfinder(
        //                surfaceRequest = request,
        //                modifier = modifier,
        //            )
        //        }
    }

    suspend fun capture(): Bitmap {
        return suspendCoroutine { continuation ->
            imageCapture.takePicture(
                Dispatchers.Main.asExecutor(),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        Log.d("camera_control", "image captured")
                        continuation.resume(image.toBitmap())
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("camera_control", "take picture failed", exception)
                    }
                },
            )
        }
    }
}