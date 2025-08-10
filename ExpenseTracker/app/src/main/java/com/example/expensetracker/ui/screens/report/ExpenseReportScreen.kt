package com.example.expensetracker.ui.screens.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.viewmodel.ExpenseReportViewModel
import com.example.expensetracker.utils.AppConstants
import com.example.expensetracker.utils.BarChart
import com.example.expensetracker.utils.ExpenseTopBar
import com.example.expensetracker.utils.buildCsv
import com.example.expensetracker.utils.last7DaysRange
import com.example.expensetracker.utils.saveToCsv
import com.example.expensetracker.utils.shareCsvFile
import com.example.expensetracker.utils.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseReportScreen(
    navController: NavController,
    viewModel: ExpenseReportViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val (start, end) = last7DaysRange()

    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()

    Scaffold(
        topBar = {
            ExpenseTopBar(
                title = stringResource(id = R.string.reports),
                navController = navController,
                showReportsButton = true,
                showListButton = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.last_seven_days_spending))
            BarChart(
                data = dailyTotals,
                barColor = MaterialTheme.colorScheme.primary,
                valueFormatter = { "₹$it" },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
            )

            Text(text = stringResource(id = R.string.category_wise_spending))
            BarChart(
                data = categoryTotals.map { it.key to it.value },
                barColor = MaterialTheme.colorScheme.tertiary,
                valueFormatter = { "₹$it" },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    scope.launch {
                        try {
                            val items = viewModel.getExpensesForRange(start, end)
                            val csv = buildCsv(items)
                            val fileName = "expenses_${System.currentTimeMillis()}.csv"

                            if (saveToCsv(context, fileName, csvContent = csv)) {
                                context.showToast(message = AppConstants.SAVED_TO_DOWNLOADS)
                            } else {
                                context.showToast(message = AppConstants.SAVE_FAILED)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            context.showToast(message = AppConstants.EXPORT_FAILED)
                        }
                    }
                }) {
                    Text(text = stringResource(id = R.string.export_csv))
                }

                Button(onClick = {
                    scope.launch {
                        try {
                            val items = viewModel.getExpensesForRange(start, end)
                            val csv = buildCsv(items)
                            val fileName = "expenses_${System.currentTimeMillis()}.csv"
                            shareCsvFile(
                                context,
                                fileName,
                                text = csv,
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            context.showToast(message = AppConstants.SHARE_FAILED)
                        }
                    }
                }) {
                    Text(text = stringResource(id = R.string.share))
                }
            }
        }
    }
}
