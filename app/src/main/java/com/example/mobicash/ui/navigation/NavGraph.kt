package com.example.mobicash.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobicash.ui.views.LoginScreen
import com.example.mobicash.ui.views.RegisterScreen
import com.example.mobicash.ui.viewmodels.LoginViewModel
import com.example.mobicash.ui.viewmodels.RegisterViewModel
import com.example.mobicash.ui.views.HomeScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {


        // LoginScreen
        composable<Login> {
            if (true){
                val loginViewModel: LoginViewModel = hiltViewModel<LoginViewModel>()
                LoginScreen(navController = navController, viewModel = loginViewModel)
            }else{
                Text("Loading..")
            }

        }

        // RegisterScreen
        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(navController = navController, viewModel = registerViewModel)
        }

        // HomeScreen
        composable<HomeApp> {
            // val registerViewModel: RegisterViewModel = hiltViewModel<RegisterViewModel>()
            HomeScreen(navController = navController)
        }

    }

}