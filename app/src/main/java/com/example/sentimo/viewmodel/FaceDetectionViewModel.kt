package com.example.sentimo.viewmodel

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

    fun processImage(image: InputImage) {
        model.detectFaces(image,
            callback = { detectedFaces ->
                _faces.postValue(detectedFaces)
            },
            errorCallback = { exception ->
                _error.postValue("Face detection failed: ${exception.message}")
            }
        )
    }
}