package ru.netology.mapsya.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.mapsya.dto.DataMapObject
import ru.netology.mapsya.repository.Repository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var counter: Long = 0L

    var currentFavoriteMapObject: MutableLiveData<DataMapObject?> =
        MutableLiveData<DataMapObject?>(null)

    var allFavoriteMapObject: MutableLiveData<List<DataMapObject>> =
        MutableLiveData<List<DataMapObject>>()

    var flagShowAll: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)


    init {
        getAll()
        flagShowAll.value = false
    }

    fun getAll() {
        viewModelScope.launch {
            allFavoriteMapObject.value = repository.getAll()
            counter = repository.getMaxId() ?: 0L
        }
    }

    fun addMapObject(dataMapObject: DataMapObject) {
        viewModelScope.launch {
            repository.addMapObject(dataMapObject)
            allFavoriteMapObject.value = repository.getAll()
            counter = repository.getMaxId() ?: 0L
        }
    }

    fun removeMapObject(id: Long) {
        viewModelScope.launch {
            repository.removeMapObject(id)
        }
        allFavoriteMapObject.value = allFavoriteMapObject.value?.let {
            it.filter { mapObject ->
                mapObject.id != id
            }
        }
    }

    fun editMapObject(dataMapObject: DataMapObject, newDescription: String) {
        viewModelScope.launch {
            repository.editMapObject(dataMapObject, newDescription)
        }

        allFavoriteMapObject.value = allFavoriteMapObject.value?.let {
            it.map { mapObject ->
                if (mapObject.id == dataMapObject.id) {
                    mapObject.copy(description = newDescription)
                } else {
                    mapObject
                }
            }
        }
        Log.d("MyLog", "editMapObject MainViewModel $allFavoriteMapObject")
    }

    fun goToPoint(dataMapObject: DataMapObject) {
        currentFavoriteMapObject.value = DataMapObject(
            id = dataMapObject.id,
            longitude = dataMapObject.longitude,
            latitude = dataMapObject.latitude,
            description = dataMapObject.description
        )
    }

    fun removeAll() {
        viewModelScope.launch {
            repository.clearAllFavorite()
            getAll()
        }
    }

    fun showAll() {
        flagShowAll.value = true
    }
}