package com.moviles.servitech.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.moviles.servitech.R

//val Inter = FontFamily(Font(R.font.inter))
val Inter = FontFamily(
    listOf(
        Font(resId = R.font.inter, weight = FontWeight.Normal)
    )
)

// Set of Material typography styles to start with
//val Typography = Typography(
//    /* Default Text styles
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//
//    bodyLarge = TextStyle(
//        fontFamily = Inter,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//    ),
//    titleLarge = TextStyle(
//        fontFamily = Inter,
//        fontWeight = FontWeight.Bold,
//        fontSize = 22.sp,
//    )
//)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
)