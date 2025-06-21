package com.moviles.servitech.view.article

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.moviles.servitech.common.Constants.CAT_ANIME
import com.moviles.servitech.model.CreateArticleRequest
import com.moviles.servitech.network.responses.subcategory.SubcategoryDto

@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun AddArticleDialog(
    currentCategory: String,
    onDismiss: () -> Unit,
    onSubmit: (CreateArticleRequest, Uri?) -> Unit,
    subcategories: List<SubcategoryDto>
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val selectedCategoryId = if (currentCategory == CAT_ANIME) 1 else 2
    var selectedSubcategory by remember { mutableStateOf<SubcategoryDto?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // filter subcategories based on the selected category
    val filteredSubcategories = subcategories.filter {
        it.category.id == selectedCategoryId
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && price.toDoubleOrNull() != null && selectedSubcategory != null) {
                        val request = CreateArticleRequest(
                            name = name,
                            description = description,
                            price = price.toDouble(),
                            category_id = selectedCategoryId,
                            subcategory_id = selectedSubcategory!!.id,
                            images = emptyList() // Se maneja la imagen por separado con Uri
                        )
                        onSubmit(request, imageUri)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Añadir artículo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") })
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") })
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") })

                DropdownMenuBox(
                    items = filteredSubcategories,
                    selectedItem = selectedSubcategory,
                    onItemSelected = { selectedSubcategory = it }
                )


                //button for image selection
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar imagen")
                }

                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    )
}

// render the dropdown menu for subcategories
@Composable
fun DropdownMenuBox(
    items: List<SubcategoryDto>,
    selectedItem: SubcategoryDto?,
    onItemSelected: (SubcategoryDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            readOnly = true,
            value = selectedItem?.name ?: "Seleccionar subcategoría",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Subcategoría") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        // show the dropdown menu when the text field is clicked
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { subcat ->
                DropdownMenuItem(
                    text = { Text(subcat.name) },
                    onClick = {
                        onItemSelected(subcat)
                        expanded = false
                    }
                )
            }
        }
    }
}
