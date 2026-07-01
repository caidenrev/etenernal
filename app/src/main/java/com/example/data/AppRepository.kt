package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AppRepository(private val appDao: AppDao) {

    val coupleProfileFlow: Flow<CoupleProfile?> = appDao.getCoupleProfileFlow()
    val allMemoriesFlow: Flow<List<Memory>> = appDao.getAllMemoriesFlow()
    val allSchedulesFlow: Flow<List<Schedule>> = appDao.getAllSchedulesFlow()
    val allWishlistFlow: Flow<List<WishlistItem>> = appDao.getAllWishlistFlow()
    val allAlertsFlow: Flow<List<AlertNotification>> = appDao.getAllAlertsFlow()

    suspend fun getCoupleProfile(): CoupleProfile? = appDao.getCoupleProfile()

    suspend fun insertCoupleProfile(profile: CoupleProfile) = appDao.insertCoupleProfile(profile)

    suspend fun updateCoupleProfile(profile: CoupleProfile) = appDao.updateCoupleProfile(profile)

    suspend fun insertMemory(memory: Memory) = appDao.insertMemory(memory)

    suspend fun deleteMemory(id: String) = appDao.deleteMemory(id)

    fun searchMemories(query: String): Flow<List<Memory>> = appDao.searchMemoriesFlow(query)

    suspend fun insertSchedule(schedule: Schedule) = appDao.insertSchedule(schedule)

    suspend fun deleteSchedule(id: String) = appDao.deleteSchedule(id)

    suspend fun insertWishlistItem(item: WishlistItem) = appDao.insertWishlistItem(item)

    suspend fun toggleWishlistItem(id: String, isCompleted: Boolean) = appDao.toggleWishlistItem(id, isCompleted)

    suspend fun deleteWishlistItem(id: String) = appDao.deleteWishlistItem(id)

    suspend fun insertAlert(alert: AlertNotification) = appDao.insertAlert(alert)

    suspend fun markAllAlertsRead() = appDao.markAllAlertsRead()

    suspend fun deleteAlert(id: String) = appDao.deleteAlert(id)

    suspend fun clearAllAlerts() = appDao.clearAllAlerts()

    // Database prepopulation logic
    suspend fun prepopulateDatabaseIfEmpty() {
        val existingProfile = getCoupleProfile()
        if (existingProfile == null) {
            // 1. Insert Profile
            val cal = Calendar.getInstance()
            cal.set(2023, Calendar.FEBRUARY, 14, 0, 0, 0) // Feb 14, 2023
            val defaultProfile = CoupleProfile(
                userId = "user_revan",
                userName = "Revan",
                userAvatar = "face",
                partnerId = "user_viona",
                partnerName = "Viona",
                partnerAvatar = "person",
                spaceId = "SPACE-LOVE-48",
                isPaired = true,
                anniversaryLong = cal.timeInMillis
            )
            insertCoupleProfile(defaultProfile)

            // 2. Insert Default Memories (matching mockups perfectly)
            // Memory A: First Anniversary
            val memory1Cal = Calendar.getInstance().apply { set(2023, Calendar.FEBRUARY, 14) }
            insertMemory(Memory(
                id = "mem_1",
                title = "First Anniversary",
                storyBlog = "Laughed our hearts out at this cute, sunlit outdoor cafe. Absolutely unforgettable afternoon!",
                location = "Tempat A",
                dateLong = memory1Cal.timeInMillis,
                photoUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop", // high-quality placeholder that matches aesthetic
                createdBy = "user_revan",
                memoryType = "Date Nights 🍷"
            ))

            // Memory B: Mountain Trip
            val memory2Cal = Calendar.getInstance().apply { set(2023, Calendar.AUGUST, 10) }
            insertMemory(Memory(
                id = "mem_2",
                title = "Mountain Trip",
                storyBlog = "Stunning sunset over the ridges. Silhouetted against the sky, feeling like the only two people in the world.",
                location = "Tempat B",
                dateLong = memory2Cal.timeInMillis,
                photoUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&auto=format&fit=crop",
                createdBy = "user_viona",
                memoryType = "Trips ✈️"
            ))

            // Memory C: Summer Treats
            val memory3Cal = Calendar.getInstance().apply { set(2023, Calendar.JULY, 5) }
            insertMemory(Memory(
                id = "mem_3",
                title = "Summer Treats",
                storyBlog = "Two giant ice cream cones clinking together. Sticky fingers, big smiles, hot summer sun!",
                location = "Tempat C",
                dateLong = memory3Cal.timeInMillis,
                photoUrl = "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=800&auto=format&fit=crop",
                createdBy = "user_revan",
                memoryType = "Date Nights 🍷"
            ))

            // 3. Insert Default Schedules
            // Schedule A: Sushi Date Night (Oct 3)
            val sched1 = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 3, 19, 0)
            }
            insertSchedule(Schedule(
                id = "sched_1",
                eventTitle = "Sushi Date Night",
                eventDateLong = sched1.timeInMillis,
                createdBy = "user_viona",
                eventType = "Food"
            ))

            // Schedule B: 5th Anniversary! (Oct 5)
            val sched2 = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 5, 0, 0)
            }
            insertSchedule(Schedule(
                id = "sched_2",
                eventTitle = "5th Anniversary!",
                eventDateLong = sched2.timeInMillis,
                createdBy = "system",
                eventType = "Milestone"
            ))

            // Schedule C: Weekend Getaway (Oct 12)
            val sched3 = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 12, 9, 0)
            }
            insertSchedule(Schedule(
                id = "sched_3",
                eventTitle = "Weekend Getaway",
                eventDateLong = sched3.timeInMillis,
                createdBy = "user_revan",
                eventType = "Travel"
            ))

            // 4. Insert Default Wishlist Items
            insertWishlistItem(WishlistItem(
                id = "wish_1",
                itemName = "Tokyo Neon Nights",
                itemDescription = "Eat ALL the ramen and visit the giant Gundam statue.",
                isCompleted = false,
                createdBy = "user_revan",
                category = "Places",
                itemCost = "$$$",
                photoUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=400&auto=format&fit=crop"
            ))

            insertWishlistItem(WishlistItem(
                id = "wish_2",
                itemName = "Cabin in the Woods",
                itemDescription = "A cozy weekend getaway just the two of us.",
                isCompleted = true,
                createdBy = "user_viona",
                category = "Places",
                itemCost = "$$",
                photoUrl = ""
            ))

            insertWishlistItem(WishlistItem(
                id = "wish_3",
                itemName = "Amalfi Coast Roadtrip",
                itemDescription = "Drive a vintage convertible along the cliffs and eat fresh pasta.",
                isCompleted = false,
                createdBy = "user_revan",
                category = "Places",
                itemCost = "$$$",
                photoUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=400&auto=format&fit=crop"
            ))

            // Activities Things to Try
            insertWishlistItem(WishlistItem(
                id = "wish_4",
                itemName = "Pottery Class",
                itemDescription = "Try throwing clay together.",
                isCompleted = false,
                createdBy = "user_viona",
                category = "Activities",
                itemCost = "$$"
            ))

            insertWishlistItem(WishlistItem(
                id = "wish_5",
                itemName = "Make Homemade Pasta",
                itemDescription = "Fettuccine from scratch in our own kitchen.",
                isCompleted = true,
                createdBy = "user_revan",
                category = "Activities",
                itemCost = "$"
            ))

            insertWishlistItem(WishlistItem(
                id = "wish_6",
                itemName = "Go Bungee Jumping",
                itemDescription = "Adrenaline rush together!",
                isCompleted = false,
                createdBy = "user_viona",
                category = "Activities",
                itemCost = "$$$"
            ))

            // 5. Insert Default Alert Notification
            insertAlert(AlertNotification(
                id = "alert_1",
                title = "Viona completed 'Make Homemade Pasta'",
                body = "She completed this activity on your wishlist. Yum!",
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                isRead = false,
                type = "wishlist_update"
            ))

            insertAlert(AlertNotification(
                id = "alert_2",
                title = "Revan added a new memory",
                body = "He added 'Summer Treats' in Date Nights 🍷.",
                timestamp = System.currentTimeMillis() - 3600000 * 12,
                isRead = true,
                type = "memory_add"
            ))
        }
    }
}
