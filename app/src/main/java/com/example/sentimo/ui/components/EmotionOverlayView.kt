package com.example.sentimo.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.sentimo.data.model.EmotionData
import com.example.sentimo.data.model.EmotionType

class EmotionOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 48f
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }
    
    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#80000000")
        style = Paint.Style.FILL
    }
    
    private var emotionData: EmotionData? = null
    
    fun updateEmotionData(emotionData: EmotionData) {
        this.emotionData = emotionData
        invalidate()
    }
    
    fun clearData() {
        emotionData = null
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        emotionData?.let { emotion ->
            drawEmotionInfo(canvas, emotion)
        }
    }
    
    private fun drawEmotionInfo(canvas: Canvas, emotion: EmotionData) {
        val emotionText = getEmotionText(emotion.overallEmotion)
        val smileText = "Smile: ${String.format("%.1f", emotion.smileProbability * 100)}%"
        val leftEyeText = "Your Left Eye: ${String.format("%.1f", emotion.leftEyeOpenProbability * 100)}% ${getEyeStatus(emotion.isLeftEyeOpen)}"
        val rightEyeText = "Your Right Eye: ${String.format("%.1f", emotion.rightEyeOpenProbability * 100)}% ${getEyeStatus(emotion.isRightEyeOpen)}"
        
        val screenHeight = height.toFloat()
        val textY = screenHeight * 0.1f + 100f
        val lineHeight = 60f
        
        paint.textSize = 48f
        val maxTextWidth = maxOf(
            paint.measureText(emotionText),
            paint.measureText(leftEyeText),
            paint.measureText(rightEyeText)
        )
        
        paint.textSize = 32f
        val smileTextWidth = paint.measureText(smileText)
        val finalMaxWidth = maxOf(maxTextWidth, smileTextWidth)
        
        val backgroundRect = RectF(
            20f, 
            screenHeight * 0.1f + 20f, 
            finalMaxWidth + 60f, 
            screenHeight * 0.1f + 320f
        )
        canvas.drawRoundRect(backgroundRect, 10f, 10f, backgroundPaint)
        
        paint.textSize = 48f
        paint.color = getEmotionColor(emotion.overallEmotion)
        canvas.drawText(emotionText, 30f, textY, paint)
        
        paint.color = Color.WHITE
        paint.textSize = 32f
        canvas.drawText(smileText, 30f, textY + lineHeight, paint)
        
        paint.color = getEyeColor(emotion.isLeftEyeOpen)
        canvas.drawText(leftEyeText, 30f, textY + lineHeight * 2, paint)
        
        paint.color = getEyeColor(emotion.isRightEyeOpen)
        canvas.drawText(rightEyeText, 30f, textY + lineHeight * 3, paint)
        
        paint.textSize = 48f
    }
    
    
    private fun getEmotionText(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.HAPPY -> "😊 Happy"
            EmotionType.SMILING -> "😄 Smiling"
            EmotionType.NEUTRAL -> "😐 Neutral"
            EmotionType.SLEEPY -> "😴 Sleepy"
            EmotionType.UNKNOWN -> "❓ Unknown"
        }
    }
    
    private fun getEmotionColor(emotion: EmotionType): Int {
        return when (emotion) {
            EmotionType.HAPPY -> Color.GREEN
            EmotionType.SMILING -> Color.YELLOW
            EmotionType.NEUTRAL -> Color.WHITE
            EmotionType.SLEEPY -> Color.BLUE
            EmotionType.UNKNOWN -> Color.GRAY
        }
    }
    
    private fun getEyeStatus(isOpen: Boolean): String {
        return if (isOpen) "👁️" else "😑"
    }
    
    private fun getEyeColor(isOpen: Boolean): Int {
        return if (isOpen) Color.GREEN else Color.RED
    }
}
