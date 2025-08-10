package com.example.expensetracker.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.entry.ExpenseEntryScreen
import com.example.expensetracker.ui.screens.list.ExpenseListScreen
import com.example.expensetracker.ui.screens.report.ExpenseReportScreen
import com.example.expensetracker.utils.AppConstants

sealed class Screen(val route: String) {
    object Entry : Screen(route = AppConstants.ENTRY)
    object List : Screen(route = AppConstants.LIST)
    object Report : Screen(route = AppConstants.REPORT)
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Entry.route) {
        composable(route = Screen.Entry.route) {
            ExpenseEntryScreen(navController, onAdded = {
                navController.navigate(
                    route = Screen.List.route
                )
            })
        }
        composable(route = Screen.List.route) { ExpenseListScreen(navController) }
        composable(route = Screen.Report.route) { ExpenseReportScreen(navController) }
    }
}
