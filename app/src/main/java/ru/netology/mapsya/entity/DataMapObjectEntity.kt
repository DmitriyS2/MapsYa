package ru.netology.mapsya.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.mapsya.dto.DataMapObject

@Entity
data class DataMapObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val longitude: Double,
    val latitude: Double,
    var description: String
) {
    fun toDto() = DataMapObject(id, longitude, latitude, description)

    companion object {
        fun fromDto(dto: DataMapObject) =
            DataMapObjectEntity(dto.id, dto.longitude, dto.latitude, dto.description)

    }
}

fun List<DataMapObjectEntity>.toDto(): List<DataMapObject> = map(DataMapObjectEntity::toDto)
