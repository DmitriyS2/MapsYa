package ru.netology.mapsya.application

import android.content.Context
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapKitModule {

    @Provides
    @Singleton
    fun provideMapKit(@ApplicationContext context: Context): MapKit {
        MapKitFactory.initialize(context)
        return MapKitFactory.getInstance()
    }
}