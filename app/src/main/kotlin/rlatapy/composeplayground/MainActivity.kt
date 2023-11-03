@file:OptIn(ExperimentalLayoutApi::class)

package rlatapy.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    companion object Route {
        private const val MainNav: String = "MainNav"
        private const val MainScreen1: String = "MainScreen1"
        private const val MainScreen2: String = "MainScreen2"
        private const val NestedNav: String = "NestedNav"
        private const val NestedScreen1: String = "NestedScreen1"
        private const val NestedScreen2: String = "NestedScreen2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val mainNavController = rememberNavController()
            val nestedNavController = rememberNavController()

            NavHost(
                navController = mainNavController,
                startDestination = MainScreen1,
                route = MainNav
            ) {
                composable(MainScreen1) {
                    Column {
                        Box(
                            Modifier
                                .fillMaxHeight(0.75f)
                                .fillMaxWidth()
                        ) {
                            NavHost(
                                navController = nestedNavController,
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
                            NavButton(mainNavController, MainScreen2) {
                                popUpTo(MainScreen1) {
                                    inclusive = true
                                    saveState = true
                                }
                            }
                            NavButton(nestedNavController, NestedScreen1)
                            NavButton(nestedNavController, NestedScreen2)
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
                        NavButton(mainNavController, MainScreen1) {
                            restoreState = true
                            popUpTo(MainScreen2) {
                                inclusive = true
                            }
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
}
