package rlatapy.composeplayground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSampleBox(
    closeBottomSheet: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    ModalBottomSheet(
        onDismissRequest = {
            println("onDismissRequest") // not called on swiping out on bottom sheet content
            closeBottomSheet()
        },
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Red),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSampleScrollableBox(
    closeBottomSheet: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    ModalBottomSheet(
        onDismissRequest = {
            println("onDismissRequest scrollable")
            closeBottomSheet()
        },
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(400.dp)
                .verticalScroll(rememberScrollState())
                .background(Color.Red),
        )
    }
}
