package com.example.sentimo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sentimo.data.model.EmotionData
import com.example.sentimo.data.model.EmotionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val _emotionData = MutableStateFlow<EmotionData?>(null)
    val emotionData: StateFlow<EmotionData?> = _emotionData.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun updateEmotionData(emotionData: EmotionData) {
        viewModelScope.launch {
            _emotionData.value = emotionData
        }
    }
    
    fun setError(message: String?) {
        viewModelScope.launch {
            _errorMessage.value = message
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        _emotionData.value = null
        _errorMessage.value = null
    }
}
