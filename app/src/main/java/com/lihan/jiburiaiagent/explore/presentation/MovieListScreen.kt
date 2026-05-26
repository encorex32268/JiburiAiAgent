package com.lihan.jiburiaiagent.explore.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.jiburiaiagent.core.presentation.ObserveAsEvents
import com.lihan.jiburiaiagent.explore.presentation.components.*
import com.lihan.jiburiaiagent.search.presentation.SearchRoot
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListRoot(
    onNavigateToDetail: (String) -> Unit,
    viewModel: MovieListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MovieListEvent.NavigateToDetail -> onNavigateToDetail(event.movieId)
            is MovieListEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    MovieListScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListState,
    onAction: (MovieListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ghibli Garden",
                        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            GlassBottomNavigation(
                activeTab = state.activeTab,
                onTabSelect = { onAction(MovieListAction.OnTabSelect(it)) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SootSpritesBackground()

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                AnimatedContent(
                    targetState = state.activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    },
                    label = "tabContentAnim",
                    modifier = Modifier.fillMaxSize()
                ) { tab ->
                    when (tab) {
                        ExploreTab.HOME -> {
                            HomeTabLayout(state = state, onAction = onAction)
                        }
                        ExploreTab.SEARCH -> {
                            SearchRoot(
                                onMovieClick = { movieId ->
                                    onAction(MovieListAction.OnMovieClick(movieId))
                                }
                            )
                        }
                        ExploreTab.WATCHLIST -> {
                            WatchlistTabLayout(state = state, onAction = onAction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabLayout(
    state: MovieListState,
    onAction: (MovieListAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        state.featuredMovie?.let { featured ->
            item {
                FeaturedHeroSection(
                    movie = featured,
                    onClick = { onAction(MovieListAction.OnMovieClick(featured.id)) }
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                CategoryTabsRow(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onSelect = { onAction(MovieListAction.OnCategorySelect(it)) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Explore Films",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable { }
                )
            }
        }

        item {
            AnimatedContent(
                targetState = state.filteredMovies,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "exploreGridAnim"
            ) { currentMovies ->
                if (currentMovies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No films found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val chunkedMovies = currentMovies.chunked(2)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        chunkedMovies.forEach { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    MovieGridCard(
                                        movie = pair[0],
                                        onClick = { onAction(MovieListAction.OnMovieClick(pair[0].id)) }
                                    )
                                }
                                if (pair.size > 1) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        MovieGridCard(
                                            movie = pair[1],
                                            onClick = { onAction(MovieListAction.OnMovieClick(pair[1].id)) }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        state.comingSoonMovie?.let { comingSoon ->
            item {
                ComingSoonSection(
                    movie = comingSoon,
                    onClick = { onAction(MovieListAction.OnMovieClick(comingSoon.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun WatchlistTabLayout(
    state: MovieListState,
    onAction: (MovieListAction) -> Unit
) {
    val watchlistMovies = remember(state.allMovies) {
        state.allMovies.filter { it.isFavorite }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (watchlistMovies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xCCFBF9F3))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(32.dp)
                ) {
                    Text(
                        text = "Your Watchlist is Empty",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explore the magical world of Ghibli and add your favorite movies to your personal watchlist!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAction(MovieListAction.OnTabSelect(ExploreTab.HOME)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Discover Films",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                item {
                    Text(
                        text = "My Watchlist",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(watchlistMovies) { movie ->
                    WatchlistHorizontalCard(
                        movie = movie,
                        onClick = { onAction(MovieListAction.OnMovieClick(movie.id)) }
                    )
                }
            }
        }
    }
}
