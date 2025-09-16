package com.example.sentimo.data.model

data class EmotionData(
    val smileProbability: Float,
    val leftEyeOpenProbability: Float,
    val rightEyeOpenProbability: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isSmiling: Boolean
        get() = smileProbability >= 0.3f
    
    val isLeftEyeOpen: Boolean
        get() = leftEyeOpenProbability >= 0.3f
    
    val isRightEyeOpen: Boolean
        get() = rightEyeOpenProbability >= 0.3f
    
    val overallEmotion: EmotionType
        get() = when {
            isSmiling && isLeftEyeOpen && isRightEyeOpen -> EmotionType.HAPPY
            isSmiling -> EmotionType.SMILING
            !isLeftEyeOpen && !isRightEyeOpen -> EmotionType.SLEEPY
            else -> EmotionType.NEUTRAL
        }
}

enum class EmotionType {
    HAPPY,
    SMILING,
    NEUTRAL,
    SLEEPY,
    UNKNOWN
}
