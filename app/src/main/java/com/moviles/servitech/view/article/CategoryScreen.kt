package com.moviles.servitech.view.article

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.moviles.servitech.R
import com.moviles.servitech.common.Constants.CAT_ANIME
import com.moviles.servitech.common.Constants.CAT_SUPPORT
import com.moviles.servitech.common.Constants.CAT_TECHNOLOGY
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.ui.components.HandleServerError
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.viewmodel.ArticleViewModel
import com.moviles.servitech.viewmodel.SubcategoryViewModel
import com.moviles.servitech.viewmodel.auth.LogoutState
import com.moviles.servitech.viewmodel.auth.LogoutViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.network.responses.article.fixedUrl
import kotlinx.coroutines.delay

import com.moviles.servitech.common.Utils.rememberSessionManager
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    navigateToDetail: (Int) -> Unit,
    vm: ArticleViewModel = hiltViewModel(),
    subcategoryVm: SubcategoryViewModel = hiltViewModel(),
    logoutViewModel: LogoutViewModel = hiltViewModel(),
    navController: NavController,
    navigateToLogin: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedSubcategoryId by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val allArticles by vm.articles.collectAsState()
    val subcategories by subcategoryVm.subcategories.collectAsState()
    val createSuccess by vm.createSuccess.collectAsState()
    val context = LocalContext.current

    val isCategoryValid = selectedCategory == CAT_ANIME || selectedCategory == CAT_TECHNOLOGY
    val sessionManager = rememberSessionManager(context)
    val user by sessionManager.user.collectAsState(initial = null)
    val token by sessionManager.token.collectAsState(initial = "")
    val logoutState by logoutViewModel.logoutState.observeAsState()

    // Carga artículos cuando cambia la categoría
    LaunchedEffect(selectedCategory) {
        vm.loadByCategory(selectedCategory)
    }

    val filteredSubcategories = remember(subcategories, selectedCategory) {
        subcategories.filter { it.category.name.equals(selectedCategory, ignoreCase = true) }
    }

    val filteredArticles = remember(allArticles, searchText, selectedSubcategoryId) {
        allArticles.filter { article ->
            val matchesText = article.name.contains(searchText, ignoreCase = true) ||
                    (article.subcategory?.name?.contains(searchText, ignoreCase = true) == true)
            val matchesSubcat = selectedSubcategoryId?.let {
                article.subcategory_id == it
            } ?: true

            matchesText && matchesSubcat
        }
    }

    val grouped = remember(filteredArticles) {
        filteredArticles.groupBy { it.subcategory?.name ?: "" }
    }

    // Local state to show logout Toast
    var showLogoutToast by remember { mutableStateOf(false) }
    var logoutError by remember { mutableStateOf<String?>(null) }

    // Handle logout state
    LaunchedEffect(logoutState) {
        when (val state = logoutState) {
            is LogoutState.Loading -> { /* You can show a loading indicator if desired */ }
            is LogoutState.Success -> {
                showLogoutToast = true
                navigateToLogin()
            }
            is LogoutState.Error -> {
                logoutError = state.message
            }
            else -> { /* No-op */ }
        }
    }

    if (showLogoutToast) {
        Toast.makeText(context, "Session closed successfully", Toast.LENGTH_LONG).show()
        showLogoutToast = false
    }
    if (logoutError != null) {
        AlertDialog(
            onDismissRequest = { logoutError = null },
            title = { Text("Error") },
            text = { Text(logoutError ?: "") },
            confirmButton = {
                TextButton(onClick = { logoutError = null }) { Text("OK") }
            }
        )
    }

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            vm.loadByCategory(selectedCategory)
            Toast.makeText(context, "Article added successfully", Toast.LENGTH_SHORT).show()
            showDialog = false
            vm.resetCreateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedCategory.replaceFirstChar { it.uppercase() }) },
                actions = {
                    IconButton(
                        onClick = {
                            if (!token.isNullOrEmpty()) {
                                logoutViewModel.logout(token.orEmpty())
                            } else {
                                Toast.makeText(context, "No Token", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isCategoryValid && user?.role?.lowercase() == "admin") {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add article")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val baseItems = listOf(
                    CAT_TECHNOLOGY to Icons.Default.Smartphone,
                    CAT_ANIME to Icons.Default.Info
                )

                // Only add the support button if the user is NOT admin.
                val items = if (user?.role?.lowercase() != "admin") {
                    baseItems + (CAT_SUPPORT to Icons.Default.Settings)
                } else {
                    baseItems
                }

                items.forEach { (cat, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = cat) },
                        label = { Text(cat.replaceFirstChar { it.uppercase() }) },
                        selected = cat == selectedCategory,
                        onClick = {
                            if (cat == CAT_SUPPORT) {
                                navController.navigate("SupportRequest")
                            } else if (selectedCategory != cat) {
                                onCategoryChange(cat)
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
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or subcategory...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSubcategories) { subcat ->
                    AssistChip(
                        onClick = {
                            selectedSubcategoryId = if (selectedSubcategoryId == subcat.id) null else subcat.id
                        },
                        label = { Text(subcat.name) },
                        leadingIcon = {
                            if (selectedSubcategoryId == subcat.id) {
                                Icon(Icons.Default.Info, contentDescription = null)
                            }
                        }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(bottom = 56.dp)
            ) {
                grouped.forEach { (subcat, list) ->
                    item {
                        Text(
                            text = subcat,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
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

    if (showDialog && isCategoryValid) {
        AddArticleDialog(
            currentCategory = selectedCategory,
            subcategories = filteredSubcategories,
            onDismiss = { showDialog = false },
            onSubmit = { request, imageUri ->
                if (request.description.length < 10) {
                    Toast.makeText(context, "Description must have at least 10 characters", Toast.LENGTH_SHORT).show()
                } else {
                    vm.createArticle(request, imageUri, selectedCategory)
                }
            }
        )
    }
}

@Composable
fun ArticleCard(article: ArticleDto, onClick: () -> Unit) {
    val imageUrl = article.images.firstOrNull()?.fixedUrl
   //import com.moviles.servitech.network.responses.article.fixedUrl

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = article.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.name.ifEmpty { "(Sin nombre)" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "₡${article.price}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

