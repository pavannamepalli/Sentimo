package com.example.sentimo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.example.sentimo.camera.CameraManager
import com.example.sentimo.face.FaceDetectionManager
import com.example.sentimo.ui.components.EmotionOverlayView
import com.example.sentimo.ui.viewmodel.MainViewModel
import com.example.sentimo.utils.PermissionUtils
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var emotionOverlayView: EmotionOverlayView
    private lateinit var cameraManager: CameraManager
    private lateinit var faceDetectionManager: FaceDetectionManager
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeManagers()
        setupObservers()
        checkPermissions()
    }

    private fun initializeViews() {
        previewView = findViewById(R.id.previewView)
        emotionOverlayView = findViewById(R.id.emotionOverlayView)
    }

    private fun initializeManagers() {
        cameraManager = CameraManager(this, this)
        faceDetectionManager = FaceDetectionManager()
        
        setupCameraCallbacks()
        setupFaceDetectionCallbacks()
    }

    private fun setupCameraCallbacks() {
        cameraManager.setImageAnalysisCallback { imageProxy ->
            faceDetectionManager.processImage(imageProxy)
        }
    }

    private fun setupFaceDetectionCallbacks() {
        faceDetectionManager.setEmotionDetectionCallback { emotionData ->
            viewModel.updateEmotionData(emotionData)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.emotionData.collect { emotionData ->
                emotionData?.let {
                    emotionOverlayView.updateEmotionData(it)
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.setError(null)
                }
            }
        }
    }

    private fun checkPermissions() {
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            startCamera()
        } else {
            requestPermissions.launch(PermissionUtils.CAMERA_PERMISSIONS)
        }
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            startCamera()
        } else {
            viewModel.setError("Camera permission is required")
        }
    }

    private fun startCamera() {
        try {
            cameraManager.startCamera(previewView)
        } catch (e: Exception) {
            viewModel.setError("Failed to start camera: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanupResources()
    }
    
    private fun cleanupResources() {
        try {
            emotionOverlayView.clearData()
            cameraManager.shutdown()
            faceDetectionManager.close()
        } catch (e: Exception) {
            // Handle cleanup errors silently
        }
    }
    
    override fun onPause() {
        super.onPause()
        cameraManager.stopCamera()
    }
    
    override fun onResume() {
        super.onResume()
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            startCamera()
        }
    }
}