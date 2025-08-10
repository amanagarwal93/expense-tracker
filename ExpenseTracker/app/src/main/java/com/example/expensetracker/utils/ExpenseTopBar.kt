package com.example.expensetracker.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.ui.navigation.Screen
import com.example.expensetracker.ui.theme.AppBarColor

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
fun ExpenseTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true,
    showReportsButton: Boolean = true,
    showListButton: Boolean = true,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (showBackButton) {
                @Composable {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            } else null
        },
        actions = {
            if (showListButton) {
                IconButton(onClick = { navController.navigate(route = Screen.List.route) }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Expense List",
                        tint = Color.White,
                    )
                }
            }
            if (showReportsButton) {
                IconButton(onClick = { navController.navigate(route = Screen.Report.route) }) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Reports",
                        tint = Color.White,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppBarColor
        )
    )
}
