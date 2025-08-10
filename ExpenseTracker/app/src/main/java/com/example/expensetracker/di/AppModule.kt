// di/AppModule.kt
package com.example.expensetracker.di

import androidx.room.Room
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.viewmodel.ExpenseEntryViewModel
import com.example.expensetracker.ui.viewmodel.ExpenseListViewModel
import com.example.expensetracker.ui.viewmodel.ExpenseReportViewModel
import com.example.expensetracker.utils.AppConstants
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            context = get(),
            klass = ExpenseDatabase::class.java,
            name = AppConstants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = false)
            .build()
    }
    single { get<ExpenseDatabase>().expenseDao() }
    single { ExpenseRepository(expenseDao = get()) }
    factory { ExpenseEntryViewModel(repo = get()) }
    factory { ExpenseListViewModel(repo = get()) }
    factory { ExpenseReportViewModel(repo = get()) }
}
