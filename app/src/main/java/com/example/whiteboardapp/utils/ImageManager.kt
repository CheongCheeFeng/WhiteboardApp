package com.example.whiteboardapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageManager(
    private val context: Context
) {
    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "shared_image.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun shareImage(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
    }

    private fun createImageFromBitmap(bitmap: Bitmap): Bitmap {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    fun shareExternal(bitmap: Bitmap) {
        val image = createImageFromBitmap(bitmap)
//        val image = setBitmapBackgroundColor(_bitmap, Color(0xffECEFF1))
        val uri = saveBitmapToFile(image)
        shareImage(uri)
    }

    fun encodeImage(bitmap: Bitmap): String{
        val image = createImageFromBitmap(bitmap)
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        val b = baos.toByteArray()
        val decodedString = Base64.encodeToString(b, Base64.DEFAULT);
        return decodedString
    }
    fun decodeImage(encodedImage: String): ImageBitmap{
        val imageAsBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
        return decodedImage.asImageBitmap()
    }
}
