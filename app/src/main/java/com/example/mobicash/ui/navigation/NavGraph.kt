package com.example.mobicash.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobicash.ui.views.LoginScreen
import com.example.mobicash.ui.views.RegisterScreen
import com.example.mobicash.ui.viewmodels.LoginViewModel
import com.example.mobicash.ui.viewmodels.RegisterViewModel


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {



        // LoginScreen
        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        // RegisterScreen
        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(navController = navController, viewModel = registerViewModel)
        }

    }

}