package com.example.whiteboardapp.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (newColor: Color) -> Unit,
) {
    val colorPickerController = rememberColorPickerController()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(370.dp)
                .padding(12.dp)
                .clip(CircleShape),
            controller = colorPickerController,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                if (colorEnvelope.fromUser) {
                    onColorSelected(colorEnvelope.color)
                }
            },
            initialColor = initialColor
        )

        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = colorPickerController,
        )
    }
}