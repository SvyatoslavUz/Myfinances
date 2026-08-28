package com.example.ui.util

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flagEmoji: String
) {
    RU(
        code = "ru",
        nativeName = "Русский",
        englishName = "Russian",
        flagEmoji = "🇷🇺"
    ),
    EN(
        code = "en",
        nativeName = "English",
        englishName = "English",
        flagEmoji = "🇬🇧"
    )
}
