package org.astroalarm.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object NatalChartWidgetEmpty {
    fun bitmap(size: Int, dark: Boolean, message: String): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (dark) 0xFF121212.toInt() else 0xFFF5F5F5.toInt())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (dark) Color.LTGRAY else Color.DKGRAY
            textSize = size * 0.06f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
        }
        canvas.drawText(message, size / 2f, size / 2f, paint)
        return bmp
    }
}
