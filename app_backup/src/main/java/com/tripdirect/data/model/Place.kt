package com.tripdirect.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Place(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val category: String,
    val rating: Double,
    val entryFee: Int,
    val entryPrice: String? = null,
    val placeId: String? = null,
    val photoUrl: String? = null,
    val photoAttribution: String? = null,
    @DrawableRes val imageRes: Int? = null,
    val vectorIcon: ImageVector? = null,
    val openingTime: String = "8:00 AM",
    val closingTime: String = "6:00 PM",
    val bestTimeToVisit: String = "All year",
    val estimatedVisitDuration: String = "2 hours",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFeatured: Boolean = false,
    val isFavorite: Boolean = false
)

