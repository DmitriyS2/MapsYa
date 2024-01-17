package ru.netology.mapsya.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.mapsya.dao.MapDao
import ru.netology.mapsya.entity.DataMapObjectEntity

@Database(entities = [DataMapObjectEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun mapDao(): MapDao

}