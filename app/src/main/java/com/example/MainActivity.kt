package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AppContent
import com.example.ui.SaaSViewModel
import com.example.ui.SaaSViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Obtain the SaaS State Management flow
    val factory = SaaSViewModelFactory(application)
    val viewModel = ViewModelProvider(this, factory)[SaaSViewModel::class.java]

    setContent {
      MyApplicationTheme {
        AppContent(viewModel = viewModel)
      }
    }
  }
}
