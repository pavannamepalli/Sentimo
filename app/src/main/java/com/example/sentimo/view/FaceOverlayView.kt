package com.example.sentimo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBox = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val paintText = Paint().apply {
        color = Color.RED
        textSize = 40f
    }

    private var faces: List<Face> = emptyList()
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f

    fun setScaleFactors(imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int) {
        val scaleX = viewWidth.toFloat() / imageWidth
        val scaleY = viewHeight.toFloat() / imageHeight

        // Maintain aspect ratio
        val minScale = minOf(scaleX, scaleY)
        this.scaleX = minScale
        this.scaleY = minScale

        // Calculate offsets to center the image
        offsetX = (viewWidth - imageWidth * minScale) / 2
        offsetY = (viewHeight - imageHeight * minScale) / 2
    }


    fun setFaces(faces: List<Face>) {
        this.faces = faces
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        faces.forEach { face ->
            drawBoundingBox(canvas, face)
            drawFaceAttributes(canvas, face)
        }
    }

    private fun drawBoundingBox(canvas: Canvas, face: Face) {
        val box = face.boundingBox
        val mappedBox = RectF(
            box.left * scaleX + offsetX,
            box.top * scaleY + offsetY,
            box.right * scaleX + offsetX,
            box.bottom * scaleY + offsetY
        )
        Log.d("FaceOverlayView", "Mapped Box: $mappedBox")
        Log.d("FaceOverlayView", "scaleX: $scaleX, scaleY: $scaleY, offsetX: $offsetX, offsetY: $offsetY")


        canvas.drawRect(mappedBox, paintBox)
    }

    private fun drawFaceAttributes(canvas: Canvas, face: Face) {
        val box = face.boundingBox
        val mappedBox = RectF(
            box.left * scaleX + offsetX,
            box.top * scaleY + offsetY,
            box.right * scaleX + offsetX,
            box.bottom * scaleY + offsetY
        )

        val smileProb = face.smilingProbability ?: 0f
        val leftEyeProb = face.leftEyeOpenProbability ?: 0f
        val rightEyeProb = face.rightEyeOpenProbability ?: 0f

        val textX = mappedBox.left
        val adjustedTextY = if (mappedBox.bottom + 150 > height) mappedBox.top - 150 else mappedBox.bottom + 50

        canvas.drawText("Smile: ${String.format("%.2f", smileProb)}", textX, adjustedTextY, paintText)
        canvas.drawText("Left Eye: ${String.format("%.2f", leftEyeProb)}", textX, adjustedTextY + 50, paintText)
        canvas.drawText("Right Eye: ${String.format("%.2f", rightEyeProb)}", textX, adjustedTextY + 100, paintText)
    }
}


