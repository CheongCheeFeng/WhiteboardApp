package com.example.whiteboardapp.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun NewDrawingDialog(
    drawingsName: List<String>,
    onDismiss: () -> Unit,
    addDrawing: (String) -> Unit,
) {
    var drawingName by remember { mutableStateOf("") }
    var isNameTaken by remember { mutableStateOf(false) }
    var isNameEmpty by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    text = "Add Drawing",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = drawingName,
                    placeholder = { Text("NewDrawing") },
                    onValueChange = { newValue ->
                        isNameTaken = false
                        isNameEmpty = false
                        drawingName = newValue.trimEnd()
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (isNameTaken) {
                    Text(
                        text = "Drawing name is taken",
                        color = Color.Red
                    )
                }

                if (isNameEmpty) {
                    Text(
                        text = "Drawing name cannot be empty",
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        when {
                            drawingsName.contains(drawingName) -> isNameTaken = true
                            drawingName.isEmpty() -> isNameEmpty = true
                            else -> {
                                addDrawing(drawingName)
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}