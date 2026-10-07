package com.example.circleedge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.circleedge.data.local.CircleEdgePreferences

class CircleEdgeViewModelFactory(
    private val preferences: CircleEdgePreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CircleEdgeViewModel::class.java)) {
            return CircleEdgeViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
