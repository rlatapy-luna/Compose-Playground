@file:OptIn(ExperimentalLayoutApi::class)

package rlatapy.composeplayground

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.lang.ref.SoftReference
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

private const val LOG_TAG: String = "nav_issue"

class MainActivity : ComponentActivity() {

    // Save and restore the state manually
    private var navState: Bundle? = null

    companion object Route {
        private const val MainNav: String = "MainNav"
        private const val MainScreen1: String = "MainScreen1"
        private const val MainScreen2: String = "MainScreen2"
        private const val NestedNav: String = "NestedNav"
        private const val NestedScreen1: String = "NestedScreen1"
        private const val NestedScreen2: String = "NestedScreen2"
    }

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appScope = CoroutineScope(Dispatchers.Main)

        setContent {

            val mainNavController = rememberNavController()
            var nestedNavController: SoftReference<NavController> = SoftReference(null)

            NavHost(
                navController = mainNavController,
                startDestination = MainScreen1,
                route = MainNav
            ) {
                composable(MainScreen1) {
                    val newNestedController = rememberNavController()
                    navState?.let {
                        newNestedController.restoreState(it)
                        navState = null
                    }
                    LaunchedEffect(newNestedController) {
                        newNestedController.addOnDestinationChangedListener { controller, destination, arguments ->
                            Log.d(
                                LOG_TAG,
                                "NestedNavController ${controller.hashCode()} dest change to ${controller.currentDestination?.route}"
                            )
                        }
                        Log.d(
                            LOG_TAG,
                            "Current nestedNavController ${nestedNavController.hashCode()} at ${nestedNavController.get()?.currentDestination?.route}"
                        )
                        Log.d(
                            LOG_TAG,
                            "New nestedNavController ${newNestedController.hashCode()} at ${newNestedController.currentDestination?.route}"
                        )
                        nestedNavController = SoftReference(newNestedController)
                    }
                    Column {
                        Box(
                            Modifier
                                .fillMaxHeight(0.75f)
                                .fillMaxWidth()
                        ) {
                            NavHost(
                                navController = newNestedController,
                                startDestination = NestedScreen1,
                                route = NestedNav
                            ) {
                                composable(NestedScreen1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Blue),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(NestedScreen1)
                                    }
                                }
                                composable(NestedScreen2) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Green),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(NestedScreen2)
                                    }
                                }
                            }
                        }
                        FlowRow(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red),
                            Arrangement.spacedBy(8.dp)
                        ) {
                            NavButton(mainNavController, MainScreen2)
                            NavButton(newNestedController, NestedScreen1)
                            NavButton(newNestedController, NestedScreen2)
                            PrintNavButton(mainNavController, newNestedController)
                        }
                    }
                }
                composable(MainScreen2) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Yellow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column {
                            Button(onClick = {
                                navState = nestedNavController.get()?.saveState()
                                mainNavController.popBackStack(MainScreen1, false)
                            }) {
                                Text("Pop to $MainScreen1")
                            }
                            Button(onClick = {
                                mainNavController.popBackStack()
                                appScope.launch {
                                    while (mainNavController.currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) != true) {
                                        yield()
                                    }
                                    nestedNavController.get()!!.navigate(NestedScreen2)
                                }
                            }) {
                                Text("popBackStack + wait resumed + nav to $NestedScreen2")
                            }
                            Button(onClick = {
                                nestedNavController.get()!!.navigate(NestedScreen2)
                                mainNavController.popBackStack()
                            }) {
                                Text("popBackStack + nav to $NestedScreen2")
                            }
                            Button(onClick = {
                                nestedNavController.get()!!.navigate(NestedScreen2)
                            }) {
                                Text("nav to $NestedScreen2 only")
                            }
                            PrintNavButton(mainNavController, nestedNavController.get())
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavButton(mainNavController: NavHostController, route: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
        Button(onClick = {
            mainNavController.navigate(route) {
                navOptions()
            }
        }) {
            Text(route)
        }
    }

    @Composable
    private fun PrintNavButton(
        mainNavController: NavController,
        nestedNavController: NavController?,
    ) {
        val context = LocalContext.current
        Button(
            onClick = {
                val navLog = buildNavigationDebugString(mainNavController, nestedNavController, context)
                Log.d(LOG_TAG, navLog)
            },
        ) {
            Text("Print navigation")
        }
    }
}

@SuppressLint("RestrictedApi")
private fun buildNavigationDebugString(
    mainNavController: NavController,
    nestedNavController: NavController?,
    context: Context,
): String {
    val navLog = StringBuilder()
    fun formatEntry(entry: NavBackStackEntry) {
        navLog.appendLine("\t• ${entry.resolveArgsToString()} [${entry.lifecycle.currentState}] [${entry.destination.id}]")
    }
    navLog.appendLine("Main nav")
    mainNavController.currentBackStack.value.forEach(::formatEntry)
    navLog.appendLine("Breadcrumb nav")
    nestedNavController?.currentBackStack?.value?.forEach(::formatEntry)
    val onBackPressedDispatcher = context.findFragmentActivity().onBackPressedDispatcher
    val field = onBackPressedDispatcher::class.declaredMemberProperties.find {
        it.name == "onBackPressedCallbacks"
    }.apply { this?.isAccessible = true }

    @Suppress("UNCHECKED_CAST")
    val callbacks: ArrayDeque<OnBackPressedCallback> = field?.getter?.call(onBackPressedDispatcher) as ArrayDeque<OnBackPressedCallback>
    navLog.appendLine()
    navLog.appendLine("Back dispatchers")
    callbacks.forEach { callback ->
        val associatedGraph = (
            callback::class.java.declaredFields.first()
                ?.apply { isAccessible = true }
                ?.get(callback) as? NavController
            )?.graph?.route
        navLog.append("\t• $callback")
        if (associatedGraph != null) {
            navLog.append(", graph = $associatedGraph)")
        }
        navLog.append(", enabled = ${callback.isEnabled}")
        navLog.appendLine()
    }
    return navLog.toString()
}

fun NavBackStackEntry.resolveArgsToString(): String? {
    var routeString = destination.route
    val args = destination.arguments
    args.forEach { arg ->
        @Suppress("DEPRECATION")
        val argValue = arguments?.get(arg.key)
        routeString = routeString?.replace("{${arg.key}}", argValue.toString())
    }

    return routeString
}

fun Context.findFragmentActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    error("Couldn't find any activity")
}
