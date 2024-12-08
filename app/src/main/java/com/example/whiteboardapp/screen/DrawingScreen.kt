package com.example.whiteboardapp.screen

import DrawingPropertiesMenu
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whiteboardapp.modal.PathProperties
import com.example.whiteboardapp.utils.DataStoreManager
import com.example.whiteboardapp.utils.ImageManager
import com.example.whiteboardapp.utils.dragMotionEvent
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

val STROKE_CAP = StrokeCap.Round
val STROKE_JOIN = StrokeJoin.Round

enum class MotionEvent {
    Idle, Down, Move, Up
}

enum class DrawMode {
    Draw, Erase
}

@Preview
@Composable
fun DrawingScreePreview() {
    DrawingScreen(drawingName = "NewDrawing")
}

@Composable
fun DrawingScreen(drawingName: String) {
    DrawingArea(drawingName)
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
private fun DrawingArea(
    drawingName: String,
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    // Datastore
    val drawingDataStore = DataStoreManager(context)
    // ImageManager
    val imageManager = ImageManager(context)
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }


    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val pathsRedoStack =
        remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }
    val captureController = rememberCaptureController()

    LaunchedEffect(key1 = Unit) {
        val encodedDrawing = drawingDataStore.getDrawing(drawingName)
        encodedDrawing?.let {
            imageBitmap = imageManager.decodeImage(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffECEFF1))
    ) {

        TopAppBar(
            title = {
                Text(drawingName)
            },
            actions = {
                IconButton(onClick = {
                    scope.launch {
                        val bitmapAsync = captureController.captureAsync()
                        val bitmap = bitmapAsync.await().asAndroidBitmap()
                        val encodedImage = imageManager.encodeImage(bitmap)

                        drawingDataStore.saveDrawing(drawingName, encodedImage)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    scope.launch {
                        val bitmapAsync = captureController.captureAsync()
                        try {
                            val bitmap = bitmapAsync.await().asAndroidBitmap()
                            val encodedImage = imageManager.encodeImage(bitmap)

                            imageManager.shareExternal(bitmap)
                            drawingDataStore.saveDrawing(drawingName, encodedImage)
                        } catch (error: Throwable) {
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null
                    )
                }
            },
        )
        val drawModifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
            .background(Color.White)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consume()

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consume()
                }
            )

        Canvas(modifier = drawModifier.capturable(captureController)) {
            when (motionEvent) {
                MotionEvent.Down -> {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)

                    previousPosition = currentPosition
                }

                MotionEvent.Move -> {
                    currentPath.quadraticTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2
                    )


                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    // Pointer is up save current path and pathPointers for undo
                    paths.add(Pair(currentPath, currentPathProperty))

                    // Reset new path, pathPointers and currentPathProperty
                    currentPath = Path()
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = currentPathProperty.color,
                        eraseMode = currentPathProperty.eraseMode
                    )

                    // Remove redo stack
                    pathsRedoStack.clear()

                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }

                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {


                val checkPoint = saveLayer(null, null)

                if (imageBitmap != null) {
                    imageBitmap?.let { drawImage(it) }
                } else {
                    drawRect(Color.White, size = size)
                }

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = STROKE_CAP,
                                join = STROKE_JOIN
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.White,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = STROKE_CAP,
                                join = STROKE_JOIN
                            ),
                        )
                    }
                }

                if (motionEvent != MotionEvent.Idle) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = STROKE_CAP,
                                join = STROKE_JOIN
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.White,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = STROKE_CAP,
                                join = STROKE_JOIN
                            ),
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }

        DrawingPropertiesMenu(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.White)
                .padding(4.dp),
            pathProperties = currentPathProperty,
            drawMode = drawMode,
            enableUndo = paths.isNotEmpty(),
            enableRedo = pathsRedoStack.isNotEmpty(),
            onClear = {
                paths.clear()
                pathsRedoStack.clear()
                imageBitmap = null
            },
            onUndo = {
                if (paths.isNotEmpty()) {

                    val lastItem = paths.last()
                    val lastPath = lastItem.first
                    val lastPathProperty = lastItem.second
                    paths.remove(lastItem)

                    pathsRedoStack.add(Pair(lastPath, lastPathProperty))
                }
            },
            onRedo = {
                if (pathsRedoStack.isNotEmpty()) {

                    val lastPath = pathsRedoStack.last().first
                    val lastPathProperty = pathsRedoStack.last().second

                    pathsRedoStack.removeLast()
                    paths.add(Pair(lastPath, lastPathProperty))
                }
            },
            onDrawModeChanged = {
                motionEvent = MotionEvent.Idle
                drawMode = it
                currentPathProperty.eraseMode = (drawMode == DrawMode.Erase)
            }
        )
    }
}


