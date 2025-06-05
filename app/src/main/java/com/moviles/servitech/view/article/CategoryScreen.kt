package com.moviles.servitech.view.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.viewmodel.ArticleViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.collectAsState
import com.moviles.servitech.common.Constants.CAT_ANIME
import com.moviles.servitech.common.Constants.CAT_SUPPORT
import com.moviles.servitech.common.Constants.CAT_TECHNOLOGY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navigateToDetail: (Int) -> Unit,
    vm: ArticleViewModel = hiltViewModel()
) {
    // Tab and search state
    var selectedCategory by remember { mutableStateOf(CAT_TECHNOLOGY) }
    var searchText by remember { mutableStateOf("") }

    // Tabs definition (with icons)
    val tabs = listOf(
        CAT_TECHNOLOGY to Icons.Default.Smartphone,
        CAT_ANIME      to Icons.Default.Info,
        CAT_SUPPORT    to Icons.Default.Settings
    )

    // When the selected category changes, we load the articles for that category
    LaunchedEffect(selectedCategory) {
        vm.loadByCategory(selectedCategory)
    }

    // Observes the articles from the ViewModel
    val allArticles by vm.articles.collectAsState()

    // Filtered articles based on the search text
    val filtered = remember(allArticles, searchText) {
        allArticles.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.subcategory?.name?.contains(searchText, ignoreCase = true) == true
        }
    }

    // Group the filtered articles by subcategory
       val grouped = remember(filtered) {
           filtered.groupBy { it.subcategory?.name ?: "(Sin subcategoría)" }
    }

    Scaffold(
        topBar = {
            // Search bar
            TopAppBar(title = { Text(selectedCategory.replaceFirstChar { it.uppercase() }) })
        },
        bottomBar = {
            // NavigationBar of Material3
            NavigationBar {
                tabs.forEach { (cat, icon) ->
                    NavigationBarItem(
                        icon      = { Icon(icon, contentDescription = cat) },
                        label     = { Text(cat.replaceFirstChar { it.uppercase() }) },
                        selected  = cat == selectedCategory,
                        onClick = {
                            if (selectedCategory != cat) {
                                selectedCategory = cat
                            }
                        }

                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value         = searchText,
                onValueChange = { searchText = it },
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder   = { Text("Buscar por nombre o subcategoría…") },
                leadingIcon   = { Icon(Icons.Default.Search, null) },
                singleLine    = true
            )

            LazyColumn(
                contentPadding       = PaddingValues(vertical = 8.dp),
                verticalArrangement  = Arrangement.spacedBy(24.dp),
                modifier             = Modifier.padding(bottom = 56.dp)
            ) {
                grouped.forEach { (subcat, list) ->
                    item {
                        Text(
                            text     = subcat,
                            style    = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding       = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list) { art ->
                                ArticleCard(art) { navigateToDetail(art.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: ArticleDto, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model               = article.images.firstOrNull()?.url,
                contentDescription  = article.name,
                modifier            = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale        = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text     = article.name.ifEmpty { "(Sin nombre)" },
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text     = "₡${article.price}",
                style    = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
