package ru.netology.mapsya

import android.app.Application
import com.yandex.mapkit.MapKitFactory


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Reading API key from BuildConfig.
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}