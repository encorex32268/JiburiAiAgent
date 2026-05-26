package com.lihan.jiburiaiagent.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lihan.jiburiaiagent.explore.presentation.ExploreTab

@Composable
fun GlassBottomNavigation(
    activeTab: ExploreTab,
    onTabSelect: (ExploreTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isHome = activeTab == ExploreTab.HOME
            IconButtonWithText(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = isHome,
                onClick = { onTabSelect(ExploreTab.HOME) }
            )

            val isSearch = activeTab == ExploreTab.SEARCH
            IconButtonWithText(
                icon = Icons.Outlined.Search,
                label = "Search",
                isSelected = isSearch,
                onClick = { onTabSelect(ExploreTab.SEARCH) }
            )

            val isWatchlist = activeTab == ExploreTab.WATCHLIST
            IconButtonWithText(
                icon = Icons.Outlined.FavoriteBorder,
                label = "Watchlist",
                isSelected = isWatchlist,
                onClick = { onTabSelect(ExploreTab.WATCHLIST) }
            )
        }
    }
}

@Composable
fun IconButtonWithText(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
