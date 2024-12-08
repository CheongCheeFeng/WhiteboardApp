package com.example.whiteboardapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whiteboardapp.composable.NewDrawingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    drawingsName: List<String>,
    addDrawing: (String) -> Unit,
    navigateTo: (String) -> Unit,
) {
    var showAddNewDrawingDialog: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        TopAppBar(title = { Text("Home") }, actions = {
            IconButton(onClick = { showAddNewDrawingDialog = !showAddNewDrawingDialog }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        })

        LazyColumn (modifier = Modifier.padding(horizontal = 8.dp)) {
            items(
                items = drawingsName,
                itemContent = {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navigateTo(it) }
                    ) {
                        Text(it)
                    }
                },
            )
        }


        if (showAddNewDrawingDialog) {
            NewDrawingDialog(
                drawingsName = drawingsName,
                onDismiss = { showAddNewDrawingDialog = !showAddNewDrawingDialog },
                addDrawing = addDrawing
            )
        }

    }
}