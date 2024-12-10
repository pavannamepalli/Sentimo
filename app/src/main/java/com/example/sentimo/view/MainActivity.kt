package com.example.sentimo.view

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sentimo.R
import com.example.sentimo.viewmodel.FaceDetectionViewModel
import com.google.firebase.FirebaseApp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewModel: FaceDetectionViewModel
    private var lastProcessedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewModel = ViewModelProvider(this)[FaceDetectionViewModel::class.java]

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(permission.CAMERA))
        }

        // Observe ViewModel
        viewModel.faces.observe(this) { faces ->
            for (face in faces) {
                val bounds = face.boundingBox
                val smileProb = face.smilingProbability ?: -1.0
                Log.d("FaceDetection", "Detected face! Bounds: $bounds, Smile Probability: $smileProb")
            }
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        // Camera Permissions

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[permission.CAMERA] == true) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxyWithInterval(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("FaceDetection", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            try {
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                // Pass ImageProxy to ViewModel for processing
                viewModel.processImage(image, imageProxy)
            } catch (e: Exception) {
                Log.e("FaceDetection", "Failed to process image: ${e.message}", e)
                imageProxy.close() // Ensure ImageProxy is closed in case of failure
            }
        } else {
            Log.e("FaceDetection", "MediaImage is null.")
            imageProxy.close()
        }
    }

    private fun processImageProxyWithInterval(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        // Process only if one second has passed since the last processing
        if (currentTime - lastProcessedTime >= 1000) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                try {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                    // Pass ImageProxy to ViewModel for processing
                    viewModel.processImage(image, imageProxy)
                } catch (e: Exception) {
                    Log.e("FaceDetection", "Failed to process image: ${e.message}", e)
                    imageProxy.close()
                }
            } else {
                Log.e("FaceDetection", "MediaImage is null.")
                imageProxy.close()
            }
            lastProcessedTime = currentTime // Update last processed time
        } else {
            imageProxy.close() // Skip frames if within the interval
        }
    }

    private fun allPermissionsGranted() = arrayOf(permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}