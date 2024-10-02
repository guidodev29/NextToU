package com.api.contigo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Navegar a MainActivity después del tiempo de espera
        lifecycleScope.launch {
            delay(3000) // Esperar 3 segundos
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish() // Finalizar SplashScreen
        }

        setContent {
            SplashScreenContent()
        }
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Reemplaza con tu logo de la aplicación
        Image(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = "Logo")
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}
