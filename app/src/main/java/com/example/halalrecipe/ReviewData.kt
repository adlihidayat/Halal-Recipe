package com.example.halalrecipe

import org.w3c.dom.Comment

data class ReviewData(
    val id: String? = null,
    val name: String? = null,
    val profile: String? = null,
    val rating: Float = 0.0f,
    val date: String? = null,
    val desc: String? = ""
)