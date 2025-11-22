package com.example.mobicash.ui.views

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobicash.ui.viewmodels.LoginViewModel

@Composable
fun HomeScreen(
    navController: NavController,
) {
    Text(
        text = "HOMEEEE", fontSize = 30.sp,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )
}