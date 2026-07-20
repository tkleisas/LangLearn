package com.langlearn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.langlearn.app.ui.screens.MainScreen
import com.langlearn.app.ui.theme.LangLearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LangLearnTheme {
                MainScreen()
            }
        }
    }
}
