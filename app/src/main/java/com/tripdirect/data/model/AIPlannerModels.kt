package com.tripdirect.data.model

object AIPlannerImages {
    const val KERALA = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=800&q=80"
    const val OOTY = "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=800&q=80"
    const val GOA = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800&q=80"
    const val HAMPI = "https://images.unsplash.com/photo-1600100397608-f010e423b971?w=800&q=80"
}

data class PopularItinerary(
    val id: String,
    val title: String,
    val duration: String,
    val startingPrice: String,
    val imageUrl: String,
    val destination: String
)

data class SmartToolItem(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val onClick: () -> Unit = {}
)

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: String = "Just now"
)

data class LanguagePhrase(
    val english: String,
    val translated: String,
    val pronunciation: String
)

data class LocalInsight(
    val destination: String,
    val bestMonths: String,
    val averageDailyBudget: String,
    val customs: String,
    val transportTips: String
)
