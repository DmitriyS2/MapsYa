package ru.netology.mapsya.viewmodel

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

    private fun getAll() {
        viewModelScope.launch {
            try {
                allFavoriteMapObject.value = repository.getAll()
                counter = repository.getMaxId() ?: 0L
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addMapObject(dataMapObject: DataMapObject) {
        viewModelScope.launch {
            try {
                repository.addMapObject(dataMapObject)
                allFavoriteMapObject.value = repository.getAll()
                counter = repository.getMaxId() ?: 0L
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeMapObject(id: Long) {
        viewModelScope.launch {
            try{
                repository.removeMapObject(id)
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
        allFavoriteMapObject.value = allFavoriteMapObject.value?.let {
            it.filter { mapObject ->
                mapObject.id != id
            }
        }
    }

    fun editMapObject(dataMapObject: DataMapObject, newDescription: String) {
        viewModelScope.launch {
            try{
                repository.editMapObject(dataMapObject, newDescription)
            } catch (e:Exception) {
                e.printStackTrace()
            }

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
            try{
                repository.clearAllFavorite()
                getAll()
            } catch (e:Exception) {
                e.printStackTrace()
            }

        }
    }

    fun showAll() {
        flagShowAll.value = true
    }
}