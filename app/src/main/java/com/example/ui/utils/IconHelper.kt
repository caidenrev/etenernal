package com.example.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconHelper {
    
    val avatarIconsMap = mapOf(
        "face" to Icons.Default.Face,
        "person" to Icons.Default.Person,
        "pets" to Icons.Default.Pets,
        "star" to Icons.Default.Star,
        "favorite" to Icons.Default.Favorite,
        "emoji_emotions" to Icons.Default.EmojiEmotions,
        "mood" to Icons.Default.Mood,
        "mood_bad" to Icons.Default.MoodBad,
        "sentiment_satisfied" to Icons.Default.SentimentSatisfied,
        "sentiment_very_satisfied" to Icons.Default.SentimentVerySatisfied,
        "child_care" to Icons.Default.ChildCare,
        "cruelty_free" to Icons.Default.CrueltyFree
    )
    
    fun getAvatarIcon(id: String): ImageVector {
        return avatarIconsMap[id] ?: Icons.Default.Face
    }
}
