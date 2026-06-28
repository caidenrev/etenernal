package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // Current screen navigation inside our Compose app
    private val _currentTab = MutableStateFlow("Dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Search query for memories
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filter memory type ("All Memories", "Date Nights 🍷", "Trips ✈️")
    private val _memoryFilter = MutableStateFlow("All Memories")
    val memoryFilter: StateFlow<String> = _memoryFilter.asStateFlow()

    // Current selected date in calendar (default: today)
    private val _selectedCalendarDate = MutableStateFlow(Calendar.getInstance())
    val selectedCalendarDate: StateFlow<Calendar> = _selectedCalendarDate.asStateFlow()

    // Current active internal notification toast
    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    // Current wishlist filter ("All", "Places", "Activities")
    private val _wishlistFilter = MutableStateFlow("All")
    val wishlistFilter: StateFlow<String> = _wishlistFilter.asStateFlow()

    init {
        // Prepopulate on startup
        viewModelScope.launch {
            repository.prepopulateDatabaseIfEmpty()
        }
    }

    // Couple Profile State Flow
    val coupleProfile: StateFlow<CoupleProfile?> = repository.coupleProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Memories State Flow (Searched and Filtered)
    val memories: StateFlow<List<Memory>> = combine(
        repository.allMemoriesFlow,
        _searchQuery,
        _memoryFilter
    ) { allMemories, query, filter ->
        var list = allMemories
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.storyBlog.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true)
            }
        }
        if (filter != "All Memories") {
            list = list.filter { it.memoryType == filter }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Schedules State Flow
    val schedules: StateFlow<List<Schedule>> = repository.allSchedulesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist State Flow (Filtered)
    val wishlist: StateFlow<List<WishlistItem>> = combine(
        repository.allWishlistFlow,
        _wishlistFilter
    ) { allWishlist, filter ->
        if (filter == "All") allWishlist
        else allWishlist.filter { it.category == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Alerts State Flow
    val alerts: StateFlow<List<AlertNotification>> = repository.allAlertsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Set active tab
    fun setTab(tabName: String) {
        _currentTab.value = tabName
    }

    // Set memory search query
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Set memory filter category
    fun setMemoryFilter(filter: String) {
        _memoryFilter.value = filter
    }

    // Set selected calendar date
    fun setSelectedCalendarDate(calendar: Calendar) {
        _selectedCalendarDate.value = calendar
    }

    // Set wishlist filter category
    fun setWishlistFilter(filter: String) {
        _wishlistFilter.value = filter
    }

    // Display temporary toast
    fun showToast(message: String) {
        _toastNotification.value = message
        viewModelScope.launch {
            kotlinx.coroutines.delay(3500)
            if (_toastNotification.value == message) {
                _toastNotification.value = null
            }
        }
    }

    fun dismissToast() {
        _toastNotification.value = null
    }

    // --- Action Methods ---

    // Insert memory
    fun addMemory(title: String, storyBlog: String, location: String, dateLong: Long, photoUrl: String, memoryType: String) {
        viewModelScope.launch {
            val user = coupleProfile.value?.userId ?: "user_revan"
            val newMemory = Memory(
                title = title,
                storyBlog = storyBlog,
                location = location,
                dateLong = dateLong,
                photoUrl = photoUrl.ifEmpty { "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=800&auto=format&fit=crop" },
                createdBy = user,
                memoryType = memoryType
            )
            repository.insertMemory(newMemory)
            showToast("Added new memory: $title! 📸")
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
            showToast("Deleted memory.")
        }
    }

    // Insert schedule
    fun addSchedule(title: String, dateLong: Long, type: String) {
        viewModelScope.launch {
            val user = coupleProfile.value?.userId ?: "user_revan"
            val newSchedule = Schedule(
                eventTitle = title,
                eventDateLong = dateLong,
                createdBy = user,
                eventType = type
            )
            repository.insertSchedule(newSchedule)
            showToast("Scheduled event: $title! 📅")
        }
    }

    fun deleteSchedule(id: String) {
        viewModelScope.launch {
            repository.deleteSchedule(id)
            showToast("Deleted event.")
        }
    }

    // Insert Wishlist item
    fun addWishlistItem(name: String, description: String, category: String, cost: String, photoUrl: String = "") {
        viewModelScope.launch {
            val user = coupleProfile.value?.userId ?: "user_revan"
            val newItem = WishlistItem(
                itemName = name,
                itemDescription = description,
                category = category,
                itemCost = cost,
                createdBy = user,
                photoUrl = photoUrl
            )
            repository.insertWishlistItem(newItem)
            showToast("Added to Wishlist: $name! ✨")
        }
    }

    fun toggleWishlistItem(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleWishlistItem(id, isCompleted)
            val item = wishlist.value.firstOrNull { it.id == id }
            if (item != null) {
                if (isCompleted) {
                    showToast("🎉 Awesome! You completed '${item.itemName}' together!")
                } else {
                    showToast("Marked '${item.itemName}' as uncompleted.")
                }
            }
        }
    }

    fun deleteWishlistItem(id: String) {
        viewModelScope.launch {
            repository.deleteWishlistItem(id)
            showToast("Removed from wishlist.")
        }
    }

    // Clear alert notifications
    fun markAllAlertsRead() {
        viewModelScope.launch {
            repository.markAllAlertsRead()
        }
    }

    fun clearAllAlerts() {
        viewModelScope.launch {
            repository.clearAllAlerts()
        }
    }

    fun deleteAlert(id: String) {
        viewModelScope.launch {
            repository.deleteAlert(id)
        }
    }

    // Re-couple or rename profiles
    fun updateProfile(yourName: String, partnerName: String, spaceId: String, anniversaryLong: Long) {
        viewModelScope.launch {
            val current = coupleProfile.value ?: CoupleProfile()
            val updated = current.copy(
                userName = yourName,
                partnerName = partnerName,
                spaceId = spaceId.ifEmpty { "SPACE-LOVE-${(10..99).random()}" },
                anniversaryLong = anniversaryLong
            )
            repository.insertCoupleProfile(updated)
            showToast("Updated space profiles! 💖")
        }
    }

    // --- Partners Actions Simulator (Simulates FCM real-time updates) ---
    fun simulatePartnerAction(actionType: String) {
        viewModelScope.launch {
            val profile = coupleProfile.value ?: CoupleProfile()
            val partnerName = profile.partnerName

            when (actionType) {
                "upload_photo" -> {
                    val memoryTitle = listOf("Romantic Dinner", "Sunset Stroll", "Beach Dayout", "Cozy Coffee Date").random()
                    val memoryLocation = listOf("The Secret Garden", "Tanjung Aan Beach", "Humble Cup Cafe", "Skyline Grill").random()
                    val photoUrl = listOf(
                        "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1494972308805-463bc619b34e?w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1510525985391-1af70ec28344?w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=800&auto=format&fit=crop"
                    ).random()

                    val newMemory = Memory(
                        id = "sim_mem_" + UUID.randomUUID().toString().take(6),
                        title = memoryTitle,
                        storyBlog = "Surprise upload from $partnerName! \"Remember that amazing time we had here? Let's do it again soon!\"",
                        location = memoryLocation,
                        dateLong = System.currentTimeMillis(),
                        photoUrl = photoUrl,
                        createdBy = profile.partnerId,
                        memoryType = "Date Nights 🍷"
                    )
                    repository.insertMemory(newMemory)

                    val alert = AlertNotification(
                        title = "$partnerName uploaded a new memory",
                        body = "Check out '$memoryTitle' she just added to your shared gallery!",
                        type = "memory_add"
                    )
                    repository.insertAlert(alert)
                    showToast("🔔 Simulating FCM Notification: Viona uploaded a photo!")
                }

                "add_schedule" -> {
                    val eventTitle = listOf("Amusement Park Date 🎡", "Double Date Night 🎬", "Cook Together Challenge 🍳", "Go Hiking ⛰️").random()
                    val dateCal = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, (2..15).random())
                    }
                    val newSchedule = Schedule(
                        id = "sim_sched_" + UUID.randomUUID().toString().take(6),
                        eventTitle = eventTitle,
                        eventDateLong = dateCal.timeInMillis,
                        createdBy = profile.partnerId,
                        eventType = listOf("Food", "Travel", "Milestone", "General").random()
                    )
                    repository.insertSchedule(newSchedule)

                    val alert = AlertNotification(
                        title = "$partnerName added an event",
                        body = "She scheduled '$eventTitle' in your Calendar.",
                        type = "schedule_add"
                    )
                    repository.insertAlert(alert)
                    showToast("🔔 Simulating FCM Notification: Viona added a calendar event!")
                }

                "complete_wishlist" -> {
                    // Find an uncompleted item
                    val uncompleted = wishlist.value.filter { !it.isCompleted }
                    if (uncompleted.isNotEmpty()) {
                        val item = uncompleted.random()
                        repository.toggleWishlistItem(item.id, true)

                        val alert = AlertNotification(
                            title = "$partnerName completed a wishlist item",
                            body = "She checked off '${item.itemName}' from your wishlist!",
                            type = "wishlist_update"
                        )
                        repository.insertAlert(alert)
                        showToast("🔔 Simulating FCM Notification: Viona checked off '${item.itemName}'!")
                    } else {
                        // Add and complete
                        val itemName = "Weekend Movie Marathon 🍿"
                        val newItem = WishlistItem(
                            id = "sim_wish_" + UUID.randomUUID().toString().take(6),
                            itemName = itemName,
                            isCompleted = true,
                            createdBy = profile.partnerId,
                            category = "Activities"
                        )
                        repository.insertWishlistItem(newItem)

                        val alert = AlertNotification(
                            title = "$partnerName completed a wishlist item",
                            body = "She added and checked off '$itemName'!",
                            type = "wishlist_update"
                        )
                        repository.insertAlert(alert)
                        showToast("🔔 Simulating FCM: Viona checked off '$itemName'!")
                    }
                }

                "add_wishlist" -> {
                    val wishlistItems = listOf(
                        "Skydiving Together 🪂" to "Activities",
                        "Staycation in a Treehouse 🌲" to "Places",
                        "Late Night Road Trip 🚗" to "Places",
                        "Go to a Jazz Concert 🎷" to "Activities"
                    )
                    val choice = wishlistItems.random()
                    val newItem = WishlistItem(
                        id = "sim_wish_add_" + UUID.randomUUID().toString().take(6),
                        itemName = choice.first,
                        createdBy = profile.partnerId,
                        category = choice.second,
                        itemCost = listOf("$", "$$", "$$$").random()
                    )
                    repository.insertWishlistItem(newItem)

                    val alert = AlertNotification(
                        title = "$partnerName added to Wishlist",
                        body = "She added '${choice.first}' into the ${choice.second} category.",
                        type = "wishlist_add"
                    )
                    repository.insertAlert(alert)
                    showToast("🔔 Simulating FCM Notification: Viona added a wishlist item!")
                }
            }
        }
    }
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
