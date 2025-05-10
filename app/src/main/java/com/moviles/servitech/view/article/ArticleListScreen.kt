package com.moviles.servitech.view.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.viewmodel.ArticleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    articleVm: ArticleViewModel = hiltViewModel(),
    navigateToArticleDetail: (Int) -> Unit
) {
    // show the article front  ViewModel
    val articles by articleVm.articles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Artículos") })
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top    = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start  = 16.dp,
                end    = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- user info section (commented out) --- logout
            /*
            item {
              Text(text = "Token: ${token.orEmpty()}", style = MaterialTheme.typography.bodySmall)
              Spacer(Modifier.height(8.dp))
              Text(text = "Usuario: ${user?.name}")
              // … resto de campos
              Spacer(Modifier.height(16.dp))
              Button(onClick = { /* logout */ }) {
                Text("Cerrar sesión")
              }
              Spacer(Modifier.height(24.dp))
            }
            */


            item {
                Text(
                    text  = "Lista de artículos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(articles) { article ->
                ArticleListItem(
                    article = article,
                    onClick = { navigateToArticleDetail(article.id) }
                )
            }
        }
    }
}

@Composable
private fun ArticleListItem(
    article: ArticleDto,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model               = article.images.firstOrNull()?.url,
                contentDescription  = article.name,
                modifier            = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale        = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = article.name.ifEmpty { "(Sin nombre)" },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = article.description,
                style    = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "₡${article.price}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
