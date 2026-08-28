package com.example.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val russianLocale = Locale("ru", "RU")
    private val englishLocale = Locale("en", "US")

    private val numberFormatterRu: NumberFormat = NumberFormat.getNumberInstance(russianLocale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }

    private val numberFormatterEn: NumberFormat = NumberFormat.getNumberInstance(englishLocale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }

    fun formatCurrency(
        amount: Double,
        currency: AppCurrency = AppCurrency.RUB,
        withPlusMinus: Boolean = false,
        isIncome: Boolean = false,
        language: AppLanguage = AppLanguage.RU
    ): String {
        val formatter = if (language == AppLanguage.EN) numberFormatterEn else numberFormatterRu
        val formattedNum = formatter.format(amount)

        val sign = if (withPlusMinus) (if (isIncome) "+" else "-") else ""

        return if (currency.isPrefix) {
            if (sign.isNotEmpty()) "$sign${currency.symbol}$formattedNum" else "${currency.symbol}$formattedNum"
        } else {
            if (sign.isNotEmpty()) "$sign$formattedNum ${currency.symbol}" else "$formattedNum ${currency.symbol}"
        }
    }

    fun formatDate(timestamp: Long, language: AppLanguage = AppLanguage.RU): String {
        val strings = AppStrings.get(language)
        val locale = if (language == AppLanguage.EN) englishLocale else russianLocale

        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        val timeFormat = SimpleDateFormat("HH:mm", locale)
        val timeString = timeFormat.format(Date(timestamp))

        return when {
            isToday -> "${strings.today}, $timeString"
            isYesterday -> "${strings.yesterday}, $timeString"
            else -> {
                val fullDateFormat = SimpleDateFormat(if (language == AppLanguage.EN) "MMM d, yyyy, HH:mm" else "d MMMM yyyy, HH:mm", locale)
                fullDateFormat.format(Date(timestamp))
            }
        }
    }

    fun formatDateGroup(timestamp: Long, language: AppLanguage = AppLanguage.RU): String {
        val strings = AppStrings.get(language)
        val locale = if (language == AppLanguage.EN) englishLocale else russianLocale

        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> strings.today
            isYesterday -> strings.yesterday
            else -> {
                val fullDateFormat = SimpleDateFormat(if (language == AppLanguage.EN) "d MMMM yyyy" else "d MMMM yyyy", locale)
                fullDateFormat.format(Date(timestamp))
            }
        }
    }

    fun formatShortDate(timestamp: Long, language: AppLanguage = AppLanguage.RU): String {
        val locale = if (language == AppLanguage.EN) englishLocale else russianLocale
        val fullDateFormat = SimpleDateFormat("d MMM", locale)
        return fullDateFormat.format(Date(timestamp))
    }
}
