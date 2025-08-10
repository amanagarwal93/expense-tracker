package com.example.expensetracker.ui.screens.entry

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.expensetracker.ui.viewmodel.ExpenseEntryViewModel
import com.example.expensetracker.ui.theme.BluePrimary
import com.example.expensetracker.utils.ExpenseTopBar
import com.example.expensetracker.utils.showToast
import org.koin.androidx.compose.koinViewModel
import com.example.expensetracker.R
import com.example.expensetracker.utils.AppConstants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseEntryViewModel = koinViewModel(),
    onAdded: () -> Unit = {}
) {
    val context = LocalContext.current

    val pickLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { viewModel.imageUri = it.toString() }
        }

    Scaffold(
        topBar = {
            ExpenseTopBar(
                title = stringResource(R.string.add_expense),
                navController = navController,
                showBackButton = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(stringResource(R.string.total_spent), style = MaterialTheme.typography.labelLarge)
            Text(
                "₹ ${"%.2f".format(viewModel.totalToday)}",
                style = MaterialTheme.typography.headlineMedium,
                color = BluePrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it.take(50) },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.amount,
                onValueChange = { viewModel.amount = it },
                label = { Text(stringResource(R.string.amount)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category simple dropdown
            var expanded by remember { mutableStateOf(false) }
            val categories = listOf(
                stringResource(R.string.category_food),
                stringResource(R.string.category_staff),
                stringResource(R.string.category_travel),
                stringResource(R.string.category_utility)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = viewModel.category,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = { viewModel.category = cat; expanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { viewModel.notes = it.take(100) },
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { pickLauncher.launch(AppConstants.IMAGE_FORMAT) }) {
                        Text(stringResource(R.string.add_receipt))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            viewModel.imageUri.isNullOrEmpty() -> stringResource(R.string.no_receipt)
                            else -> stringResource(R.string.receipt_added)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!viewModel.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = viewModel.imageUri?.toUri(),
                        contentDescription = stringResource(R.string.receipt_image),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }


            Spacer(modifier = Modifier.height(18.dp))

            // Submit with small animation: animateContentSize on top container
            val animModifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
            Box(modifier = animModifier) {
                Button(
                    onClick = {
                        viewModel.addExpense {
                            context.showToast(AppConstants.EXPENSE_ADDED)
                            onAdded()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_expense))
                }
            }
        }
    }
}
