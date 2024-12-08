import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.whiteboardapp.R
import com.example.whiteboardapp.composable.BrushSizeDialog
import com.example.whiteboardapp.composable.ColorPickerDialog
import com.example.whiteboardapp.modal.PathProperties
import com.example.whiteboardapp.screen.DrawMode

@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    enableUndo: Boolean,
    enableRedo: Boolean,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {

    val properties by rememberUpdatedState(newValue = pathProperties)

    var showColorDialog by remember { mutableStateOf(false) }
    var showBrushSizeDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { onClear() }
        ) {
            Icon(
                Icons.Filled.CleaningServices,
                contentDescription = null,
                tint = Color.Black
            )
        }
        IconButton(
            onClick = {
                currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                    DrawMode.Draw
                } else {
                    DrawMode.Erase
                }
                onDrawModeChanged(currentDrawMode)
            }
        ) {
            when (currentDrawMode == DrawMode.Draw) {
                true -> Icon(
                    painter = painterResource(id = R.drawable.ic_eraser_black_24dp),
                    contentDescription = null,
                    tint = Color.Gray
                )

                else -> Icon(
                    Icons.Filled.Brush,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }


        IconButton(onClick = { showColorDialog = !showColorDialog }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_color_circle),
                contentDescription = null,)
        }

        IconButton(onClick = { showBrushSizeDialog = !showBrushSizeDialog }) {
            Icon(Icons.Filled.Circle, contentDescription = null, tint = properties.color)
        }

        IconButton(onClick = {
            onUndo()
        }) {
            Icon(
                Icons.AutoMirrored.Filled.Undo,
                contentDescription = null,
                tint = if (enableUndo) Color.DarkGray else Color.LightGray
            )
        }

        IconButton(onClick = {
            onRedo()
        }) {
            Icon(
                Icons.AutoMirrored.Filled.Redo,
                contentDescription = null,
                tint = if (enableRedo) Color.DarkGray else Color.LightGray
            )
        }
    }

    if (showColorDialog) {
        ColorPickerDialog(
            properties.color,
            onDismiss = { showColorDialog = !showColorDialog },
            onNegativeClick = { showColorDialog = !showColorDialog },
            onPositiveClick = { color: Color ->
                showColorDialog = !showColorDialog
                properties.color = color
            }
        )
    }

    if (showBrushSizeDialog) {
        BrushSizeDialog(properties) {
            showBrushSizeDialog = !showBrushSizeDialog
        }
    }
}