package com.freeraspkmp.android.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log

internal object AppIconUtil {
    private fun compressBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    internal fun getAppIconAsBase64String(context: Context, packageName: String): String? {
        try{
            val packageManager = context.packageManager
            val drawable = packageManager.getApplicationIcon(packageName)

            if(drawable is BitmapDrawable && drawable.bitmap != null) {
                return compressBitmap(drawable.bitmap)
            }

            if(drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return compressBitmap(bitmap)
            }
            return null
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("freeraspKMP", "App not found for package: $packageName")
            return null
        } catch (e: Exception) {
            Log.e("freeraspKMP", "Could not retrieve app icon for $packageName: ${e.message}")
            return null
        }

    }
}