package com.example.sentimo.model

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionModel {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(options)

    fun detectFaces(image: InputImage, callback: (List<Face>) -> Unit, errorCallback: (Exception) -> Unit) {
        detector.process(image)
            .addOnSuccessListener { faces ->
                callback(faces)
            }
            .addOnFailureListener { e ->
                errorCallback(e)
            }
    }
}