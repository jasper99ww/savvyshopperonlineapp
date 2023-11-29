package com.example.savvyshopperonlineapp

import androidx.annotation.DrawableRes

object Utils {
    val category = listOf(
        Category(title = "Drinks", resId = R.drawable.drinks, id = 0),
        Category(title = "Fruits", resId = R.drawable.fruits, id = 1),
        Category(title = "Vegetables", resId = R.drawable.vegetables, id = 2),
        Category(title = "Meat", resId = R.drawable.meat, id = 3),
        Category(title = "Dairy", resId = R.drawable.dairy, id = 4),
        Category(title = "Bakery", resId = R.drawable.bakery, id = 5),
        Category(title = "Hygiene", resId = R.drawable.hygiene, id = 6),
        Category(title = "Cleaning", resId = R.drawable.cleaning, id = 7),
        Category(title = "Other", resId = R.drawable.other, id = 10001),
    )
}

data class Category(
    @DrawableRes val resId: Int = -1,
    val title: String = "",
    val id: Int = -1,
)