package com.tripdirect.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Attraction(
    val id: String,
    val name: String,
    val destination: String,
    val state: String = "",
    val category: String = "Attraction",
    val rating: Double = 4.5,
    val entryPrice: String? = null,
    val entryFee: Int = 0,
    val placeId: String? = null,
    val photoUrl: String? = null,
    val photoAttribution: String? = null,
    val description: String = "",
    @DrawableRes val imageRes: Int? = null,
    val vectorIcon: ImageVector? = null,
    val location: String = destination
)

