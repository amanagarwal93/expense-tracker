package com.example.expensetracker.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.expensetracker.data.local.ExpenseEntity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun todayRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    val end = calendar.timeInMillis - 1
    return start to end
}

fun saveToCsv(
    context: Context,
    fileName: String,
    csvContent: String
): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, AppConstants.MIME_TYPE)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException(AppConstants.MEDIA_STORE_CREATION_FAILED)

            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray())
                outputStream.flush()
            } ?: throw IOException(AppConstants.OUTPUT_STREAM_OPEN_FAILED)

            // Mark the file as finished
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

        } else {
            // Pre-Android Q - direct save to Downloads folder
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            file.writeText(text = csvContent)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun shareCsvFile(
    context: Context,
    fileName: String,
    text: String,
    mimeType: String = AppConstants.MIME_TYPE,
    chooserTitle: String = AppConstants.SHARE_TITLE
) {
    try {
        // Save file in cache directory
        val cacheFile = File(context.cacheDir, fileName)
        cacheFile.writeText(text)

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, cacheFile)


        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    } catch (e: Exception) {
        e.printStackTrace()
        context.showToast(message = AppConstants.FILE_SHARING_ERROR)
    }
}

fun buildCsv(items: List<ExpenseEntity>): String {
    val fmt = SimpleDateFormat(AppConstants.DATE_FORMAT_WITH_TIME, Locale.getDefault())
    val sb = StringBuilder()
    sb.append("title,amount,category,date,notes\n")
    items.forEach { it ->
        val title = escapeCsv(value = it.title)
        val notes = escapeCsv(value = it.notes ?: "")
        val date = fmt.format(Date(it.dateEpochMs))
        sb.append("$title,${it.amount},${it.category},$date,$notes\n")
    }
    return sb.toString()
}

private fun escapeCsv(value: String): String {
    val needsQuotes =
        value.contains(other = ",") || value.contains(other = "\"") || value.contains(other = "\n")
    var v = value.replace(oldValue = "\"", newValue = "\"\"")
    if (needsQuotes) v = "\"$v\""
    return v
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun last7DaysRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    val end = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, -6)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val start = calendar.timeInMillis
    return start to end
}

fun getLast7DayRanges(): List<Triple<String, Long, Long>> {
    val df = SimpleDateFormat(AppConstants.DATE_PATTERN, Locale.getDefault())
    val ranges = mutableListOf<Triple<String, Long, Long>>()

    for (offset in 6 downTo 0) {
        val calendarStart = Calendar.getInstance()
        calendarStart.add(Calendar.DAY_OF_YEAR, -offset)
        calendarStart.set(Calendar.HOUR_OF_DAY, 0)
        calendarStart.set(Calendar.MINUTE, 0)
        calendarStart.set(Calendar.SECOND, 0)
        calendarStart.set(Calendar.MILLISECOND, 0)
        val start = calendarStart.timeInMillis

        val calendarEnd = Calendar.getInstance()
        calendarEnd.timeInMillis = start
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23)
        calendarEnd.set(Calendar.MINUTE, 59)
        calendarEnd.set(Calendar.SECOND, 59)
        calendarEnd.set(Calendar.MILLISECOND, 999)
        val end = calendarEnd.timeInMillis

        val label = df.format(Date(start))
        ranges.add(Triple(first = label, second = start, third = end))
    }
    return ranges
}

