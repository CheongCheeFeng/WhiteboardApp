package com.example.whiteboardapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whiteboardapp.screen.DrawingScreen
import com.example.whiteboardapp.screen.HomeScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    drawingsName: List<String>,
    addNewDrawing: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier.statusBarsPadding(),
        navController = navController,
        startDestination = "startDestination"
    ) {

        composable(route = "startDestination") { _ ->
            HomeScreen(modifier, drawingsName, addNewDrawing) { route ->
                navController.navigate(route)
            }
        }


        drawingsName.forEach {
            composable(route = it) { _ ->
                Column(Modifier.navigationBarsPadding()) {
                    DrawingScreen(it)
                }
            }
        }
    }
}