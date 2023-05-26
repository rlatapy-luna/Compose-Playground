package rlatapy.composeplayground

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import org.junit.Test

class MainActivityTest() {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun toggle_test(): Unit = runAndroidComposeUiTest<MainActivity> {
        waitUntilExactlyOneExists(
            hasText("toggle")
        )
        onNodeWithText("toggle")
            .assertIsDisplayed()
            .performClick()
        waitUntilExactlyOneExists(
            hasText("text 2")
        )
        onRoot().printToLog("lazy_debug")
        onNodeWithText("text 2")
            .assertIsDisplayed()
    }
}
