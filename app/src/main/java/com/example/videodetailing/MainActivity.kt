package com.example.videodetailing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.videodetailing.data.MainViewModel
import com.example.videodetailing.data.UiState
import com.example.videodetailing.model.VideoDetails
import com.example.videodetailing.ui.theme.VideoDetailingTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoDetailingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var videoId by remember { mutableStateOf("") }
    val videoState by remember { viewModel.videoDetails }.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = videoId,
            onValueChange = { videoId = it },
            label = { Text("Enter Video ID") },
            placeholder = { Text("e.g., dQw4w9WgXcQ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                if (videoId.isNotEmpty()) {
                    viewModel.getVideoDetails(videoId.trim())
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = videoId.isNotEmpty()
        ) {
            Text("Submit")
        }

        if (videoState != null) {
            keyboardController?.hide()
            when (videoState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    VideoInfo(videoDetails = (videoState as UiState.Success<VideoDetails>).data)
                }

                is UiState.Error -> {
                    Text(
                        text = "Error: ${(videoState as UiState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun VideoInfo(videoDetails: VideoDetails) {
    val scrollState = rememberScrollState()

    if (videoDetails.items.isNullOrEmpty()) {
        Toast.makeText(LocalContext.current, "No video details found", Toast.LENGTH_SHORT).show()
        return
    }

    val videoDetails = videoDetails.items[0]
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AsyncImage(
            model = videoDetails.snippet.thumbnails.high.url,
            contentDescription = "Thumbnail",
            modifier = Modifier.size(300.dp)
        )

        Text(
            text = "Title:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = videoDetails.snippet.title,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Description:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = videoDetails.snippet.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 10,
            overflow = TextOverflow.Visible
        )

        Text(
            text = "Release Date:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = formatDate(videoDetails.snippet.publishedAt),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat =
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: return dateString.substring(0, 10))
    } catch (_: Exception) {
        try {
            dateString.substring(0, 10)
        } catch (_: Exception) {
            dateString
        }
    }
}