package com.example.savvyshopperonlineapp.database.room

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideShopSpotDatabase(app: Application): ShopSpotDatabase {
        return Room.databaseBuilder(
            app,
            ShopSpotDatabase::class.java,
            "shop_spots.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideShopSpotRepository(db: ShopSpotDatabase): ShopSpotRepository {
        return ShopSpotRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}