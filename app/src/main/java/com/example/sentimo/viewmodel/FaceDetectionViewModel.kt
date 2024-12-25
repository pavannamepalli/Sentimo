package com.example.sentimo.viewmodel

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sentimo.model.FaceDetectionModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

class FaceDetectionViewModel : ViewModel() {
    private val model = FaceDetectionModel()

    private val _faces = MutableLiveData<List<Face>>()
    val faces: LiveData<List<Face>> = _faces

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun processImage(image: InputImage, imageProxy: ImageProxy) {
        model.detectFaces(
            image,
            imageProxy,
            callback = { detectedFaces ->
                Log.d("FaceDetection", "Detected Faces: $detectedFaces")
                _faces.postValue(detectedFaces)
            },
            errorCallback = { exception ->
                _error.postValue("Face detection failed: ${exception.message}")
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        model.close()
    }
}
