package com.example.mobicash.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object Inicio {
    const val route = "inicio"
}

@Serializable
object Profile

@Serializable
data class Detail(val id: Int)