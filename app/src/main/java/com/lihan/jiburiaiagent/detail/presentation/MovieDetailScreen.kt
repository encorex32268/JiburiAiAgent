package com.lihan.jiburiaiagent.detail.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.jiburiaiagent.core.presentation.ObserveAsEvents
import com.lihan.jiburiaiagent.detail.presentation.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailRoot(
    onNavigateBack: () -> Unit,
    viewModel: MovieDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showCustomToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MovieDetailEvent.NavigateBack -> onNavigateBack()
            is MovieDetailEvent.ShowToast -> {
                toastMessage = event.message.asString(context)
                scope.launch {
                    showCustomToast = true
                    delay(3000)
                    showCustomToast = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MovieDetailScreen(
            state = state,
            onAction = viewModel::onAction
        )

        // Custom Ghibli-themed floating animated Toast
        AnimatedVisibility(
            visible = showCustomToast,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
                .zIndex(99f)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xE6FBF9F3))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = toastMessage,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailScreen(
    state: MovieDetailState,
    onAction: (MovieDetailAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        state.movie?.let { movie ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // PARALLAX HERO HEADER BANNER
                item {
                    DetailHeroHeader(
                        movie = movie,
                        onBack = { onAction(MovieDetailAction.OnBackClick) }
                    )
                }

                // FLOATING TITLE & METADATA CARD (OVERLAPPING BANNER)
                item {
                    DetailTitleCard(movie = movie)
                }

                // PLOT DESCRIPTION
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = movie.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // DIRECTOR'S VISION AND QUOTE CARD
                item {
                    DirectorQuoteCard(movie = movie)
                }
            }

            // STICKY BOTTOM WATCHLIST FAB BUTTON
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                WatchlistStickyButton(
                    isOnWatchlist = state.isOnWatchlist,
                    onClick = { onAction(MovieDetailAction.OnToggleWatchlist) }
                )
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
