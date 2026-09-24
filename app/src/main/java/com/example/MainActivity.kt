package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AlarmLockState

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }

  /**
   * While an alarm mission is on screen the volume / media keys are swallowed here, so the
   * alarm cannot be turned down or muted without completing the mission. When no alarm is
   * ringing this is a no-op and the keys behave normally.
   */
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (AlarmLockState.handleKeyEvent(event)) return true
    return super.dispatchKeyEvent(event)
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
