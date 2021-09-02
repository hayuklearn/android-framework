package com.af.demo.compose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.af.demo.compose.ui.theme.AndroidComposeTheme
import java.io.File

class EntryActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AndroidComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
          Greeting("Android")
        }
      }
    }
    val files = File("D:\\").listFiles()
    files?.map {
      println(it)
    }
  }
}

@Composable
fun Header() {

  Greeting(name = "Hayuk")
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  showBackground = true
)
@Composable
fun HeaderPreview() {
  AndroidComposeTheme {
    Header()
  }
}

@Composable
fun Greeting(name: String) {
  Surface(color = Color.Black) {
    Text(text = "Hello $name!", color = Color.Red, modifier = Modifier.padding(12.dp))
  }
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  showBackground = true
)
@Composable
fun GreetingPreview() {
  AndroidComposeTheme {
    Greeting("Android")
  }
}

@Composable
fun Counter() {
  val count = remember {
    mutableStateOf(0)
  }
  Button(onClick = { count.value++ }) {
    Text(text = "${count.value}")
  }
}

@Preview
@Composable
fun CounterPreview() {
  Counter()
}
