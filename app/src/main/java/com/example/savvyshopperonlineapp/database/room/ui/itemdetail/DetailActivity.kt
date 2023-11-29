package com.example.savvyshopperonlineapp.database.room.ui.itemdetail

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.AppCompatActivity
//

class DetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent.getIntExtra("item_id", -1)

        setContent {
            DetailScreen(
                id = itemId,
                navigateUp = { finish() }
            )
        }
    }
}
