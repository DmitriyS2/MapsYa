package ru.netology.mapsya.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.mapsya.entity.DataMapObjectEntity

@Dao
interface MapDao {

    @Query("SELECT * FROM DataMapObjectEntity ORDER BY id DESC")
    suspend fun getAll(): List<DataMapObjectEntity>

    @Query("SELECT MAX(id) FROM DataMapObjectEntity")
    suspend fun getMaxId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dataMapObjectEntity: DataMapObjectEntity)

    @Query("DELETE FROM DataMapObjectEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """UPDATE DataMapObjectEntity SET 
                description = :description                            
                WHERE id = :id"""
    )
    suspend fun updateDescriptionById(id: Long, description: String)

    @Query("DELETE FROM DataMapObjectEntity")
    suspend fun removeAll()
}