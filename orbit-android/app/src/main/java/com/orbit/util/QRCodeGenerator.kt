package com.orbit.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * QR Code generation utilities
 */
object QRCodeGenerator {

    /**
     * Generate a QR code bitmap from a string
     *
     * @param content The content to encode in the QR code
     * @param size The size of the QR code in pixels (width and height)
     * @param foregroundColor The color of the QR code pattern (default: black)
     * @param backgroundColor The background color (default: white)
     * @return Bitmap of the generated QR code
     */
    fun generateQRCode(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) foregroundColor else backgroundColor
                    )
                }
            }

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate a pod join QR code
     * Formats the join code as: flowpods://join?code=XXXX
     *
     * @param joinCode The plain join code from the backend
     * @param size The QR code size in pixels
     * @return Bitmap of the QR code
     */
    fun generatePodJoinQRCode(
        joinCode: String,
        size: Int = 512
    ): Bitmap? {
        val formattedCode = "flowpods://join?code=$joinCode"
        return generateQRCode(formattedCode, size)
    }

    /**
     * Extract join code from scanned QR data
     * Handles both plain codes and formatted URLs
     *
     * @param scannedData The scanned QR code content
     * @return The extracted join code, or the original data if not formatted
     */
    fun extractJoinCode(scannedData: String): String {
        return when {
            scannedData.startsWith("flowpods://join?code=") -> {
                scannedData.substringAfter("code=").substringBefore("&")
            }
            scannedData.contains("code=") -> {
                scannedData.substringAfter("code=").substringBefore("&")
            }
            else -> scannedData
        }
    }
}

