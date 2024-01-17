package ru.netology.mapsya.repository

import ru.netology.mapsya.dto.DataMapObject

interface Repository {

    suspend fun getAll(): List<DataMapObject>

    suspend fun getMaxId(): Long?

    suspend fun addMapObject(dataMapObject: DataMapObject)

    suspend fun removeMapObject(id:Long)

    suspend fun editMapObject(dataMapObject: DataMapObject, newDescription:String)

    suspend fun clearAllFavorite()
}