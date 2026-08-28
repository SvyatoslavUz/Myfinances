package com.example.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class IconOption(
    val key: String,
    val name: String,
    val icon: ImageVector
)

object CategoryIcons {
    val availableIcons = listOf(
        IconOption("shopping_cart", "Магазин", Icons.Default.ShoppingCart),
        IconOption("restaurant", "Обед / Ресторан", Icons.Default.Restaurant),
        IconOption("fastfood", "Фастфуд", Icons.Default.Fastfood),
        IconOption("local_cafe", "Кофе", Icons.Default.LocalCafe),
        IconOption("smoking", "Вредные привычки", Icons.Default.SmokingRooms),
        IconOption("money_off", "Долг / Выплаты", Icons.Default.MoneyOff),
        IconOption("credit_card", "Кредит / Карты", Icons.Default.CreditCard),
        IconOption("work", "Работа / Зарплата", Icons.Default.Work),
        IconOption("family", "Семья / Родные", Icons.Default.People),
        IconOption("laptop", "Фриланс / Компьютер", Icons.Default.Laptop),
        IconOption("trending_up", "Инвестиции", Icons.Default.TrendingUp),
        IconOption("card_giftcard", "Подарки", Icons.Default.CardGiftcard),
        IconOption("directions_car", "Транспорт / Авто", Icons.Default.DirectionsCar),
        IconOption("home", "Дом / Жилье", Icons.Default.Home),
        IconOption("local_hospital", "Здоровье / Аптека", Icons.Default.LocalHospital),
        IconOption("sports_esports", "Развлечения / Игры", Icons.Default.SportsEsports),
        IconOption("checkroom", "Одежда / Покупки", Icons.Default.Checkroom),
        IconOption("account_balance", "Банк / Счета", Icons.Default.AccountBalance),
        IconOption("payments", "Платежи", Icons.Default.Payments),
        IconOption("flight", "Путешествия", Icons.Default.Flight),
        IconOption("school", "Образование / Курсы", Icons.Default.School),
        IconOption("fitness_center", "Спорт / Фитнес", Icons.Default.FitnessCenter),
        IconOption("pets", "Питомцы", Icons.Default.Pets),
        IconOption("receipt", "Счета / Квитанции", Icons.Default.Receipt),
        IconOption("add_circle", "Доход (плюс)", Icons.Default.AddCircle),
        IconOption("remove_circle", "Расход (минус)", Icons.Default.RemoveCircle)
    )

    fun getIcon(key: String): ImageVector {
        return when (key) {
            "shopping_cart" -> Icons.Default.ShoppingCart
            "restaurant" -> Icons.Default.Restaurant
            "fastfood" -> Icons.Default.Fastfood
            "local_cafe" -> Icons.Default.LocalCafe
            "smoking" -> Icons.Default.SmokingRooms
            "money_off" -> Icons.Default.MoneyOff
            "credit_card" -> Icons.Default.CreditCard
            "work" -> Icons.Default.Work
            "family" -> Icons.Default.People
            "laptop" -> Icons.Default.Laptop
            "trending_up" -> Icons.Default.TrendingUp
            "card_giftcard" -> Icons.Default.CardGiftcard
            "directions_car" -> Icons.Default.DirectionsCar
            "home" -> Icons.Default.Home
            "local_hospital" -> Icons.Default.LocalHospital
            "sports_esports" -> Icons.Default.SportsEsports
            "checkroom" -> Icons.Default.Checkroom
            "account_balance" -> Icons.Default.AccountBalance
            "payments" -> Icons.Default.Payments
            "flight" -> Icons.Default.Flight
            "school" -> Icons.Default.School
            "fitness_center" -> Icons.Default.FitnessCenter
            "pets" -> Icons.Default.Pets
            "receipt" -> Icons.Default.Receipt
            "add_circle" -> Icons.Default.AddCircle
            "remove_circle" -> Icons.Default.RemoveCircle
            else -> Icons.Default.Payments
        }
    }
}
