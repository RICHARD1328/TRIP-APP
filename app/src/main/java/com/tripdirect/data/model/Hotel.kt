package com.tripdirect.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Hotel(
    val id: String,
    val name: String,
    val destination: String,
    val location: String,
    val description: String,
    val rating: Double,
    val reviewCount: Int,
    val pricePerNight: Int,
    @DrawableRes val imageRes: Int? = null,
    val vectorIcon: ImageVector? = null,
    val distanceFromCenter: Double,
    val facilities: List<String>,
    val roomType: String,
    val cancellationPolicy: String,
    val breakfastIncluded: Boolean,
    val isFavorite: Boolean
)
