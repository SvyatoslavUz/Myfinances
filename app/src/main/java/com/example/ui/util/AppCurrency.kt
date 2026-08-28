package com.example.ui.util

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val nameRu: String,
    val nameEn: String,
    val isPrefix: Boolean
) {
    RUB(
        code = "RUB",
        symbol = "₽",
        nameRu = "Российский рубль (₽)",
        nameEn = "Russian Ruble (₽)",
        isPrefix = false
    ),
    USD(
        code = "USD",
        symbol = "$",
        nameRu = "Доллар США ($)",
        nameEn = "US Dollar ($)",
        isPrefix = true
    ),
    EUR(
        code = "EUR",
        symbol = "€",
        nameRu = "Евро (€)",
        nameEn = "Euro (€)",
        isPrefix = false
    ),
    UZS(
        code = "UZS",
        symbol = "сум",
        nameRu = "Узбекский сум (UZS)",
        nameEn = "Uzbek Som (UZS)",
        isPrefix = false
    ),
    KZT(
        code = "KZT",
        symbol = "₸",
        nameRu = "Казахстанский тенге (₸)",
        nameEn = "Kazakhstani Tenge (₸)",
        isPrefix = false
    ),
    CNY(
        code = "CNY",
        symbol = "¥",
        nameRu = "Китайский юань (¥)",
        nameEn = "Chinese Yuan (¥)",
        isPrefix = true
    );

    fun getDisplayName(language: AppLanguage): String {
        return if (language == AppLanguage.EN) nameEn else nameRu
    }
}
