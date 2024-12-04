package com.example.puttiponggrinpoon

import java.io.Serializable

data class Coffee(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
) : Serializable
