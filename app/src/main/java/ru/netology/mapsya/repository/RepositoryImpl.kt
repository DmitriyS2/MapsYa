package ru.netology.mapsya.repository

import ru.netology.mapsya.dao.MapDao
import ru.netology.mapsya.dto.DataMapObject
import ru.netology.mapsya.entity.DataMapObjectEntity
import ru.netology.mapsya.entity.toDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val mapDao: MapDao
) : Repository {

    override suspend fun getAll() = mapDao.getAll().toDto()

    override suspend fun getMaxId(): Long? = mapDao.getMaxId()

    override suspend fun addMapObject(dataMapObject: DataMapObject) =
        mapDao.insert(DataMapObjectEntity.fromDto(dataMapObject))

    override suspend fun removeMapObject(id: Long) = mapDao.removeById(id)

    override suspend fun editMapObject(dataMapObject: DataMapObject, newDescription: String) =
        mapDao.updateDescriptionById(dataMapObject.id, newDescription)

    override suspend fun clearAllFavorite() = mapDao.removeAll()

}