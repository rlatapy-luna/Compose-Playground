package rlatapy.composeplayground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoroutineTest {

    @Test
    fun immediate_cancel_test(): TestResult = runTest {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        var hasRun = false

        val job = scope.launch {
            delay(1_000)

            withContext(NonCancellable) {
                delay(3_000)
                hasRun = true
            }
        }

        val job2 = scope.launch {
            delay(500)
            job.cancelAndJoin()
        }

        job2.join()

        assertFalse(hasRun)
    }

    @Test
    fun no_cancel_test(): TestResult = runTest {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        var hasRun = false

        val job = scope.launch {
            delay(1_000)

            withContext(NonCancellable) {
                delay(3_000)
                hasRun = true
            }
        }

        val job2 = scope.launch {
            delay(1_500)
            job.cancelAndJoin()
        }

        job2.join()

        assertTrue(hasRun)
    }
}
