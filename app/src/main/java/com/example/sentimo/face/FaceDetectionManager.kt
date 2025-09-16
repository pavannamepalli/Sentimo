package com.example.sentimo.face

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.example.sentimo.data.model.EmotionData

class FaceDetectionManager {
    
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        
        FaceDetection.getClient(options)
    }
    
    private var onEmotionDetected: ((EmotionData) -> Unit)? = null
    fun setEmotionDetectionCallback(callback: (EmotionData) -> Unit) {
        onEmotionDetected = callback
    }
    
    fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage, 
                imageProxy.imageInfo.rotationDegrees
            )
            
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    handleDetectedFaces(faces)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    
    private fun handleDetectedFaces(faces: List<Face>) {
        for (face in faces) {
            val emotionData = extractEmotionData(face)
            onEmotionDetected?.invoke(emotionData)
        }
    }
    
    private fun extractEmotionData(face: Face): EmotionData {
        return EmotionData(
            smileProbability = face.smilingProbability ?: 0f,
            leftEyeOpenProbability = face.rightEyeOpenProbability ?: 0f,
            rightEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
        )
    }
    
    fun close() {
        try {
            faceDetector.close()
        } catch (e: Exception) {
            // Handle cleanup errors silently
        }
    }
}
