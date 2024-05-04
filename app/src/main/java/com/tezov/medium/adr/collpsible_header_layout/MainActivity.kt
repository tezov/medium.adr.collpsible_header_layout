package com.tezov.medium.adr.collpsible_header_layout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.medium.adr.collpsible_header_layout.ui.theme.MediumTheme
import kotlin.random.Random

fun generateRandomColor(): Color {
    val random = Random.Default
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return Color(red, green, blue)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediumTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColumnCollapsibleHeader(
                        modifier = Modifier.fillMaxSize(),
                        properties = ColumnCollapsibleHeader.Properties(
                            min = 40.dp,
                            max = 120.dp
                        ),
                        header = { progress, progressDp ->
                            Header(progress = progress, progressDp = progressDp)
                        },
                        body = {
                            (1..20).forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .padding(vertical = 2.dp)
                                        .background(generateRandomColor())
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

private const val DIVIDER_HEADER_VISIBILITY_START = 0.3f

@Composable
fun BoxScope.Header(
    progress: Float,
    progressDp: Dp
) {
    Column(
        modifier = Modifier.height(progressDp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            modifier = Modifier
                .padding(
                    start = 8.dp + (24.dp * progress),
                    end = 8.dp,
                    top = 4.dp,
                    bottom = 4.dp
                ),
            text = "Header Title",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = (16 + ((54 - 14) * progress)).sp
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(
                    if(progress >= DIVIDER_HEADER_VISIBILITY_START) {
                        0.0f
                    }
                    else {
                        (DIVIDER_HEADER_VISIBILITY_START - progress) / DIVIDER_HEADER_VISIBILITY_START
                    }
                ),
            thickness = 4.dp,
            color = Color.Black
        )

    }
}

