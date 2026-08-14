package com.tripdirect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.model.Review
import com.tripdirect.data.model.ReviewCategory
import com.tripdirect.data.model.ReviewSortOption
import com.tripdirect.data.model.ReviewSummary
import com.tripdirect.data.repository.MockReviewRepository
import com.tripdirect.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ReviewsViewModel(
    private val repository: ReviewRepository = MockReviewRepository()
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _reviewSummary = MutableStateFlow<ReviewSummary?>(null)
    val reviewSummary: StateFlow<ReviewSummary?> = _reviewSummary.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ReviewCategory.ALL)
    val selectedCategory: StateFlow<ReviewCategory> = _selectedCategory.asStateFlow()

    private val _selectedRatingFilter = MutableStateFlow<Int?>(null)
    val selectedRatingFilter: StateFlow<Int?> = _selectedRatingFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(ReviewSortOption.RELEVANT)
    val selectedSort: StateFlow<ReviewSortOption> = _selectedSort.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _verifiedOnly = MutableStateFlow(false)
    val verifiedOnly: StateFlow<Boolean> = _verifiedOnly.asStateFlow()

    private val _photosOnly = MutableStateFlow(false)
    val photosOnly: StateFlow<Boolean> = _photosOnly.asStateFlow()

    private val _selectedReview = MutableStateFlow<Review?>(null)
    val selectedReview: StateFlow<Review?> = _selectedReview.asStateFlow()

    private val _userReviews = MutableStateFlow<List<Review>>(emptyList())
    val userReviews: StateFlow<List<Review>> = _userReviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var currentEntityId: String = "ooty_botanical_garden"

    init {
        loadReviews("ooty_botanical_garden")
        loadUserReviews()
    }

    fun loadReviews(entityId: String) {
        currentEntityId = entityId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _reviews.value = repository.getReviews(entityId)
                _reviewSummary.value = repository.getReviewSummary(entityId)
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load reviews."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserReviews() {
        viewModelScope.launch {
            try {
                _userReviews.value = repository.getUserReviews("current_user")
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun selectCategory(category: ReviewCategory) {
        _selectedCategory.value = category
    }

    fun filterByRating(rating: Int?) {
        _selectedRatingFilter.value = rating
    }

    fun searchReviews(query: String) {
        _searchQuery.value = query
    }

    fun sortReviews(sortOption: ReviewSortOption) {
        _selectedSort.value = sortOption
    }

    fun toggleVerifiedOnly() {
        _verifiedOnly.value = !_verifiedOnly.value
    }

    fun togglePhotosOnly() {
        _photosOnly.value = !_photosOnly.value
    }

    fun selectReview(review: Review?) {
        _selectedReview.value = review
    }

    fun markHelpful(reviewId: String) {
        viewModelScope.launch {
            val updatedCount = repository.markHelpful(reviewId)
            _reviews.value = _reviews.value.map {
                if (it.id == reviewId) it.copy(helpfulCount = updatedCount) else it
            }
            if (_selectedReview.value?.id == reviewId) {
                _selectedReview.value = _selectedReview.value?.copy(helpfulCount = updatedCount)
            }
            _statusMessage.value = "Marked review as helpful 👍"
        }
    }

    fun reportReview(reviewId: String, reason: String) {
        viewModelScope.launch {
            repository.reportReview(reviewId, reason)
            _statusMessage.value = "Thank you. Review reported for \"$reason\"."
        }
    }

    fun submitReview(
        entityId: String,
        entityName: String,
        category: ReviewCategory,
        rating: Float,
        title: String,
        comment: String,
        photos: List<String>
    ) {
        viewModelScope.launch {
            val newReview = Review(
                id = "rev_${UUID.randomUUID().toString().take(8)}",
                userName = "You (Traveler)",
                rating = rating,
                title = title,
                comment = comment,
                date = "Today",
                category = category,
                entityId = entityId.ifEmpty { currentEntityId },
                entityName = entityName.ifEmpty { "Ooty Botanical Garden" },
                helpfulCount = 0,
                photos = photos,
                verifiedVisit = true
            )
            val added = repository.submitReview(newReview)
            _reviews.value = listOf(added) + _reviews.value
            _userReviews.value = listOf(added) + _userReviews.value
            _statusMessage.value = "✓ Review submitted successfully!"
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            _reviews.value = _reviews.value.filter { it.id != reviewId }
            _userReviews.value = _userReviews.value.filter { it.id != reviewId }
            _statusMessage.value = "Review deleted."
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getFilteredReviews(): List<Review> {
        var list = _reviews.value

        // Category filter
        if (_selectedCategory.value != ReviewCategory.ALL) {
            list = list.filter { it.category == _selectedCategory.value }
        }

        // Rating filter
        _selectedRatingFilter.value?.let { targetRating ->
            list = list.filter { Math.round(it.rating) == targetRating }
        }

        // Verified filter
        if (_verifiedOnly.value) {
            list = list.filter { it.verifiedVisit }
        }

        // Photos filter
        if (_photosOnly.value) {
            list = list.filter { it.photos.isNotEmpty() }
        }

        // Search query filter
        if (_searchQuery.value.isNotBlank()) {
            val q = _searchQuery.value.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.comment.lowercase().contains(q) ||
                        it.userName.lowercase().contains(q)
            }
        }

        // Sorting
        return when (_selectedSort.value) {
            ReviewSortOption.RELEVANT -> list
            ReviewSortOption.NEWEST -> list.reversed()
            ReviewSortOption.HIGHEST_RATING -> list.sortedByDescending { it.rating }
            ReviewSortOption.LOWEST_RATING -> list.sortedBy { it.rating }
            ReviewSortOption.MOST_HELPFUL -> list.sortedByDescending { it.helpfulCount }
        }
    }
}
