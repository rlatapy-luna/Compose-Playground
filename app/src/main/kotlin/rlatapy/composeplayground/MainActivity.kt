package rlatapy.composeplayground

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val options = BitmapFactory.Options().apply {
            inScaled = false
        }
        val source = BitmapFactory.decodeResource(
            resources,
            R.drawable.image,
            options
        )

        val matrix1 = Matrix()
        matrix1.setScale(2f, 2f)
        val bitmap1 = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix1, true)

        val matrix2 = Matrix()
        matrix2.setTranslate(200f, 200f)
        val bitmap2 = Bitmap.createBitmap(source, 200, 200, source.width - 200, source.height - 200, matrix2, true)

        val matrix3 = Matrix()
        matrix3.setScale(2f, 2f)
        matrix3.postTranslate(200f, 200f)
        val bitmap3 = Bitmap.createBitmap(source, 200, 200, source.width - 200, source.height - 200, matrix3, true)

        setContent {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("Source ${source.width}x${source.height}")
                Image(painter = BitmapPainter(source.asImageBitmap()), contentDescription = "")
                Spacer(modifier = Modifier.height(8.dp))

                Text("bitmap1 ${bitmap1.width}x${bitmap1.height}\n$matrix1")
                Image(painter = BitmapPainter(bitmap1.asImageBitmap()), contentDescription = "")
                Spacer(modifier = Modifier.height(8.dp))

                Text("bitmap2 ${bitmap2.width}x${bitmap2.height}\n$matrix2")
                Image(painter = BitmapPainter(bitmap2.asImageBitmap()), contentDescription = "")
                Spacer(modifier = Modifier.height(8.dp))

                Text("bitmap3 ${bitmap3.width}x${bitmap3.height}\n$matrix3")
                Image(painter = BitmapPainter(bitmap3.asImageBitmap()), contentDescription = "")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
