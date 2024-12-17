package com.example.halalrecipe

import com.google.firebase.Timestamp

data class MenuData(
    val id: String? = null,
    val author: String? = null,
    val category: String? = null,
    val createdAt: Timestamp? = null,
    val imageAuthor: String? = null,
    val imageFood: String? = null,
    val ingredients: List<String>? = null,
    val rating: Double? = null,
    val title: String? = null
)
