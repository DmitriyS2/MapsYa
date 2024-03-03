package ru.netology.mapsya.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapsya.R
import ru.netology.mapsya.databinding.ItemDataMapObjectBinding
import ru.netology.mapsya.dto.DataMapObject


interface Listener {
    fun deleteMapObject(id: Long)
    fun editMapObject(dataMapObject: DataMapObject, newDescription: String)
    fun goToMapObject(dataMapObject: DataMapObject)
}

class MapObjectAdapter(private val listener: Listener) :
    ListAdapter<DataMapObject, MapObjectAdapter.MapObjectHolder>(MapObjectDiffCallback()) {

    var mapObjectList = emptyList<DataMapObject>()

    class MapObjectHolder(item: View, private val listener: Listener) :
        RecyclerView.ViewHolder(item) {
        private val binding = ItemDataMapObjectBinding.bind(item)

        private lateinit var dataMapObj: DataMapObject

        fun bind(payload: Payload) {
            payload.description?.let {
                binding.descPoint.text = it
                dataMapObj = dataMapObj.copy(description = it)
            }
        }

        fun bind(dataMapObject: DataMapObject) = with(binding) {
            dataMapObj = dataMapObject
            descPoint.text = dataMapObject.description
            longitudePoint.text = dataMapObject.longitude.toString()
            latitudePoint.text = dataMapObject.latitude.toString()
            groupEditDescription.visibility = View.GONE

            buttonEditPoint.setOnClickListener {
                ObjectAnimator.ofPropertyValuesHolder(
                    buttonEditPoint,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
                editDescPoint.setText(dataMapObject.description)
                groupEditDescription.visibility = View.VISIBLE
            }

            buttonDeletePoint.setOnClickListener {
                ObjectAnimator.ofPropertyValuesHolder(
                    buttonDeletePoint,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
                listener.deleteMapObject(dataMapObject.id)
            }

            buttonOkEditDescPoint.setOnClickListener {
                if (!editDescPoint.text.isNullOrEmpty()) {
                    groupEditDescription.visibility = View.GONE
                    listener.editMapObject(dataMapObject, editDescPoint.text.toString())
                } else {
                    editDescPoint.error = "Поле не может быть пустым"
                }
            }

            cardViewFavorite.setOnClickListener {
                listener.goToMapObject(dataMapObj)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapObjectHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_map_object, parent, false)
        return MapObjectHolder(view, listener)
    }

    override fun onBindViewHolder(
        holder: MapObjectHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                if (it is Payload) {
                    holder.bind(it)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MapObjectHolder, position: Int) {
        val dataMapObject = getItem(position)
        holder.bind(dataMapObject)
    }

    override fun getItemCount(): Int {
        return mapObjectList.size
    }
}

class MapObjectDiffCallback : DiffUtil.ItemCallback<DataMapObject>() {
    override fun areItemsTheSame(oldItem: DataMapObject, newItem: DataMapObject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataMapObject, newItem: DataMapObject): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DataMapObject, newItem: DataMapObject): Any =
        Payload(
            description = newItem.description.takeIf { oldItem.description != it },
        )
}

data class Payload(
    val id: Int? = null,
    val description: String? = null,
)