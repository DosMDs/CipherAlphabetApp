package com.dosmds.cipheralphabet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dosmds.cipheralphabet.ui.screens.ConverterScreen
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CipherAlphabetAppTheme {
                ConverterScreen()
            }
        }
    }
}
