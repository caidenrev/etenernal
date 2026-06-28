package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "couple_profile")
data class CoupleProfile(
    @PrimaryKey val id: Int = 1,
    val userId: String = "user_revan",
    val userName: String = "Revan",
    val userAvatar: String = "🦊",
    val partnerId: String = "user_viona",
    val partnerName: String = "Viona",
    val partnerAvatar: String = "🐰",
    val spaceId: String = "SPACE-LOVE-99",
    val isPaired: Boolean = true,
    val anniversaryLong: Long = 1676332800000L, // 14 Feb 2023 default
    val fcmToken: String = ""
)

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val storyBlog: String,
    val location: String,
    val dateLong: Long,
    val photoUrl: String, // String path or url
    val createdBy: String, // UID of creator
    val memoryType: String = "General" // "Date Night", "Trip", "General"
)

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val eventTitle: String,
    val eventDateLong: Long,
    val createdBy: String,
    val eventType: String = "General" // "Milestone", "Food", "General"
)

@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemName: String,
    val itemDescription: String = "",
    val isCompleted: Boolean = false,
    val createdBy: String,
    val category: String = "General", // "Places", "Activities", "General"
    val itemCost: String = "$", // "$", "$$", "$$$"
    val photoUrl: String = "" // Optional preview image
)

@Entity(tableName = "alerts")
data class AlertNotification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "general"
)
