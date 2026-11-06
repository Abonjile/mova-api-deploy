package com.example.mova.utils

import com.example.mova.R
import com.example.mova.data.remote.models.BadgeDto

/**
 * Extension function to map a BadgeDto's icon descriptor/name 
 * to its corresponding local drawable resource ID.
 */
fun BadgeDto.getBadgeDrawable(): Int {
    return when (iconUrl) {
        "ic_number_one_trophy" -> R.drawable.ic_number_one_trophy
        "ic_golden_medal" -> R.drawable.ic_golden_medal
        "ic_golden_trophy" -> R.drawable.ic_golden_trophy
        "ic_red_medal" -> R.drawable.ic_red_medal
        else -> R.drawable.ic_red_medal
    }
}