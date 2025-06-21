package com.moviles.servitech.view.article


import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.moviles.servitech.viewmodel.ArticleViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.moviles.servitech.viewmodel.SubcategoryViewModel
import androidx.compose.ui.text.input.KeyboardType
import com.moviles.servitech.model.CreateArticleRequest
import com.moviles.servitech.network.responses.article.fixedUrl
import com.moviles.servitech.viewmodel.utils.FileHelper

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull



// muestra el detalle de un artículo específico con una interfaz de usuario que permite editar y eliminar el artículo
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ArticleDetailScreen(
    articleId: Int,
    currentCategory: String,
    navController: NavHostController,
    navigateBack: () -> Unit,
    viewModel: ArticleViewModel = hiltViewModel(),
    subcategoryVm: SubcategoryViewModel = hiltViewModel(),


) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    val article by viewModel.articleById.collectAsState()
    val subcategories by subcategoryVm.subcategories.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedSubcategoryId by remember { mutableStateOf<Int?>(null) }
    var subcategoryExpanded by remember { mutableStateOf(false) }

    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    LaunchedEffect(articleId) {
        viewModel.loadArticleById(articleId)
        subcategoryVm.getSubcategoriesByCategory(currentCategory)
    }

    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(article) {
        article?.let {
            name = it.name
            description = it.description
            price = it.price.toString()
            selectedSubcategoryId = it.subcategory_id
        }
    }

    val filteredSubcategories = subcategories.filter {
        it.category.name.equals(currentCategory, ignoreCase = true)
    }

    // Launcher to pick an image from the gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

// Handle the update success state to show a toast message
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
            isEditing = false
            viewModel.loadArticleById(articleId)
            viewModel.resetUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del artículo") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        )

        {
            Log.d("IMAGEN_ARTICULO", "article: $article")
            val imageUrl = article?.images?.firstOrNull()?.fixedUrl
            //import com.moviles.servitech.network.responses.article.fixedUrl
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (article == null && errorMessage == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("No article found")
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading article",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { 
                                viewModel.reloadArticleById(articleId)
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                article?.let { art ->
                    if (isEditing) {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Seleccionar imagen")
                        }
                    }


                    Log.d("IMAGEN_ARTICULO", "URL IMAGEN COMPLETA: $imageUrl")



                        // show the image if available
                        val displayImage = imageUri ?: imageUrl

                        displayImage?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = "Imagen del artículo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(bottom = 16.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    // Input fields for article details

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF005F73),
                        unfocusedBorderColor = Color.LightGray,
                        disabledContainerColor = Color(0xFFFAFAFA),
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.DarkGray
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        enabled = isEditing,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        enabled = isEditing,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = isEditing,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = currentCategory,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        enabled = false,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )


                    if (isEditing) {
                        val selectedSubcatName = filteredSubcategories.find { it.id == selectedSubcategoryId }?.name ?: "Seleccionar"
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            OutlinedTextField(
                                value = selectedSubcatName,
                                onValueChange = {},
                                label = { Text("Subcategoría") },
                                enabled = false,
                                readOnly = true,
                                colors = fieldColors,
                                modifier = Modifier.fillMaxWidth().clickable { subcategoryExpanded = true }


                            )
                            DropdownMenu(
                                expanded = subcategoryExpanded,
                                onDismissRequest = { subcategoryExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                filteredSubcategories.forEach { subcat ->
                                    DropdownMenuItem(
                                        text = { Text(subcat.name) },
                                        onClick = {
                                            selectedSubcategoryId = subcat.id
                                            subcategoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = art.subcategory.name,
                            onValueChange = {},
                            label = { Text("Subcategoría") },
                            enabled = false,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(


                        onClick = {
                                if (isEditing) {
                                    val request = CreateArticleRequest(
                                        name = name,
                                        description = description,
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        category_id = art.category.id,
                                        subcategory_id = selectedSubcategoryId ?: art.subcategory_id,
                                        images = emptyList() // o una lista vacía si ya estás manejando la imagen por separado
                                    )

                                    viewModel.updateArticleWithImage(
                                        id = art.id,
                                        request = request,
                                        imageUri = imageUri, // image URI from the picker
                                        category = currentCategory,
                                        onSuccess = {
                                            Toast.makeText(context, "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                    )

                                } else {
                                    isEditing = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text(if (isEditing) "Guardar" else "Editar")
                        }


                        var showConfirm by remember { mutableStateOf(false) }

                        if (showConfirm) {
                            AlertDialog(
                                onDismissRequest = { showConfirm = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showConfirm = false
                                        viewModel.deleteArticle(articleId, currentCategory) {
                                            Toast.makeText(context, "Artículo eliminado correctamente", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }

                                    }) {
                                        Text("Sí, eliminar")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirm = false }) {
                                        Text("Cancelar")
                                    }
                                },
                                title = { Text("¿Estás seguro?") },
                                text = { Text("Esta acción eliminará permanentemente el artículo.") }
                            )
                        }

                        Button(
                            onClick = { showConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}
