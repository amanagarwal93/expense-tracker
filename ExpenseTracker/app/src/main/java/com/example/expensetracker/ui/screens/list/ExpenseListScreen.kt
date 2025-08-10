package com.example.expensetracker.ui.screens.list

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.viewmodel.ExpenseListViewModel
import com.example.expensetracker.utils.AppConstants
import com.example.expensetracker.utils.ExpenseTopBar
import com.example.expensetracker.utils.GroupBy
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
fun ExpenseListScreen(
    navController: NavController,
    viewModel: ExpenseListViewModel = koinViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val total by viewModel.total.collectAsState()

    var expanded by remember { mutableStateOf(value = false) }
    var group by remember { mutableStateOf(value = GroupBy.NONE) }

    val calendar = Calendar.getInstance()
    var showPicker by remember { mutableStateOf(value = false) }

    if (showPicker) {
        DatePickerDialog(
            LocalContext.current, { _, y, m, d ->
                val start = Calendar.getInstance()
                    .apply {
                        set(y, m, d, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                val end = Calendar.getInstance()
                    .apply {
                        set(y, m, d, 23, 59, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis
                viewModel.setRange(start, end)
                showPicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            ExpenseTopBar(
                title = stringResource(id = R.string.expenses),
                navController = navController,
                showBackButton = true,
                showListButton = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .padding(all = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.expenses),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "${stringResource(id = R.string.total)}${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(height = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Button(onClick = { viewModel.setToday() }) { Text(text = stringResource(id = R.string.today)) }
                Button(onClick = {
                    showPicker = true
                }) { Text(text = stringResource(id = R.string.pick_date)) }
                Spacer(modifier = Modifier.weight(weight = 1f))
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(
                            text = "${stringResource(id = R.string.group)}${
                                group.name.lowercase().replaceFirstChar { it.uppercase() }
                            }"
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(id = R.string.select_group)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = AppConstants.GROUP_NONE) },
                            onClick = {
                                group = GroupBy.NONE
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = AppConstants.GROUP_CATEGORY) },
                            onClick = {
                                group = GroupBy.CATEGORY
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = AppConstants.GROUP_DATE) },
                            onClick = {
                                group = GroupBy.DATE
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(height = 12.dp))

            if (expenses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.no_expense),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        stringResource(id = R.string.add_some_expense),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                return@Column
            }

            // Group logic
            val groupedExpenses = when (group) {
                GroupBy.NONE -> mapOf(stringResource(id = R.string.all_expenses) to expenses)
                GroupBy.CATEGORY -> expenses.groupBy { it.category }
                GroupBy.DATE -> expenses.groupBy {
                    SimpleDateFormat(
                        AppConstants.DATE_FORMAT,
                        Locale.getDefault()
                    ).format(Date(it.dateEpochMs))
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                groupedExpenses.forEach { (header, items) ->
                    val groupTotal = items.sumOf { it.amount }
                    val groupCount = items.size

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$groupCount ${stringResource(id = R.string.group_count)}${
                                    "%.2f".format(
                                        groupTotal
                                    )
                                }",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    items(items) { e ->
                        ExpenseRow(e)
                    }
                }
            }
        }
    }
}


@Composable
fun ExpenseRow(e: com.example.expensetracker.data.local.ExpenseEntity) {
    val df = SimpleDateFormat(AppConstants.HOUR_FORMAT, Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = e.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${e.category} • ${df.format(Date(e.dateEpochMs))}",
                    style = MaterialTheme.typography.bodySmall
                )
                e.notes?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹ ${"%.2f".format(e.amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
