package com.example.whiteboardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.whiteboardapp.ui.theme.WhiteboardAppTheme
import com.example.whiteboardapp.utils.DataStoreManager
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhiteboardAppTheme {
                Column(Modifier.safeContentPadding()) {
                    val scope = rememberCoroutineScope()
                    val pathDataStore = DataStoreManager(LocalContext.current)
                    val drawingKeys = remember { mutableStateListOf<String>() }

                    LaunchedEffect(key1 = drawingKeys) {
                        pathDataStore.getAllDrawings().forEach {
                            drawingKeys.add(it.name)
                        }
                    }
                    NavGraph(drawingsName = drawingKeys.toList(), addNewDrawing = {
                        scope.launch {
                            pathDataStore.initDrawing(it)
                        }
                        drawingKeys.add(it)
                    })
                }
            }
        }
    }
}