package com.theost.tike.ui.widgets

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import com.google.zxing.BarcodeFormat.QR_CODE
import com.google.zxing.qrcode.QRCodeWriter


object QrCodeGenerator {

    fun generateBitmap(size: Int, color: Int, content: String): Bitmap {
        val bits = QRCodeWriter().encode(content, QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, ARGB_8888).also { bitmap ->
            for (x in 0 until size) {
                for (y in 0 until size) {
                    if (bits[x, y]) bitmap.setPixel(x, y, color)
                }
            }
        }
    }
}