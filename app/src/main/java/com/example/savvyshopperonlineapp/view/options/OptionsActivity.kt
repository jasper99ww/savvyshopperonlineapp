package com.example.savvyshopperonlineapp.view.options

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.savvyshopperonlineapp.database.room.DataStoreManager

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataStoreManager.getInstance(this)
            setContent {
                OptionsScreen(onBack = { finish() })
            }
        }
    }
}
