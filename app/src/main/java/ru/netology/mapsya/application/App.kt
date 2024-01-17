package ru.netology.mapsya.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import ru.netology.mapsya.BuildConfig

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Reading API key from BuildConfig.
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}