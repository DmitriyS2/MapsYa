package ru.netology.mapsya.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mapsya.R
import ru.netology.mapsya.databinding.FragmentMapsBinding
import ru.netology.mapsya.dto.DataMapObject
import ru.netology.mapsya.viewmodel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment() {

    @Inject
    lateinit var mapKitN: MapKit

    private lateinit var binding: FragmentMapsBinding

    private lateinit var mapWindow: MapWindow
    private lateinit var map: Map
    private lateinit var mapView: MapView
    private lateinit var userLocation: UserLocationLayer

    private lateinit var imageProvider: ImageProvider

    private val viewModel: MainViewModel by activityViewModels()

    private val sizeChangedListener = SizeChangedListener { _, _, _ ->
        // Recalculate FocusRect and FocusPoint on every map's size change
        updateFocusInfo()
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView.map?.move(CameraPosition(it, 15F, 0F, 0F), START_ANIMATION, null)
            }
            userLocation.setObjectListener(null)
        }
    }

    // удаление метки
    private val placemarkTapListener = MapObjectTapListener { mapObject, point ->
        //   mapObject.isVisible = false
        mapView.map.mapObjects.remove(mapObject)
        Toast.makeText(
            activity,
            "Удалена метка (${point.longitude}, ${point.latitude})",
            Toast.LENGTH_SHORT
        ).show()
        viewModel.removeMapObject(mapObject.userData as Long)
        true
    }

    private val inputListener = object : InputListener {
        // добавить метку по долгому нажатию на карту
        override fun onMapLongTap(map: Map, point: Point) {
            binding.groupInputDesc.visibility = View.VISIBLE
            binding.buttonOkInputDesc.setOnClickListener {
                if (!binding.inputDescriptionPoint.text.isNullOrEmpty()) {
                    val text = binding.inputDescriptionPoint.text.toString()
                    viewModel.addMapObject(
                        DataMapObject(
                            id = 0L,
                            longitude = point.longitude,
                            latitude = point.latitude,
                            description = text
                        )
                    )
                    val placemark = mapView.map.mapObjects.addPlacemark().apply {
                        geometry = Point(point.latitude, point.longitude)
                        setIcon(imageProvider)
                        setText(binding.inputDescriptionPoint.text.toString())
                        userData = viewModel.counter
                    }
                    placemark.addTapListener(placemarkTapListener)

                    binding.groupInputDesc.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "Установлена метка ${binding.inputDescriptionPoint.text.toString()}\n(${point.longitude}, ${point.latitude})",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.inputDescriptionPoint.setText("")
                }
            }
        }

        override fun onMapTap(map: Map, point: Point) {
            Toast.makeText(
                activity,
                "Координаты места: ${point.longitude}, ${point.latitude}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // добавить метку из dataMapObject
    private fun createMapObject(dataMapObject: DataMapObject) {
        val placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(dataMapObject.latitude, dataMapObject.longitude)
            setIcon(imageProvider)
            setText(dataMapObject.description)
            Log.d("MyLog", "createMapObject доб метку desc=${dataMapObject.description}")
            userData = dataMapObject.id
        }
        placemark.addTapListener(placemarkTapListener)

        Toast.makeText(
            activity,
            "Установлена метка ${dataMapObject.description}\n(${dataMapObject.longitude}, ${dataMapObject.latitude})",
            Toast.LENGTH_SHORT
        ).show()
    }

    private val geoObjectTapListener = GeoObjectTapListener {
        // Move camera to selected geoObject
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            map.move(position, SMOOTH_ANIMATION, null)
        }
        val selectionMetadata =
            it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        map.selectGeoObject(selectionMetadata)
        Toast.makeText(
            activity,
            if (it.geoObject.name != null) "${it.geoObject.name}" else "Нет данных об объекте",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    userLocation.isVisible = true
                    userLocation.isHeadingEnabled = false
                    userLocation.cameraPosition()?.target?.also {
                        val map = mapView.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Необходимо разрешение",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)

        imageProvider = ImageProvider.fromResource(context, R.drawable.pin_green)
        mapView = binding.mapview

        mapWindow = mapView.mapWindow
        map = mapWindow.map

        val trafficJam = mapKitN.createTrafficLayer(mapWindow)
        trafficJam.isTrafficVisible = true

        mapWindow.addSizeChangedListener(sizeChangedListener)
        updateFocusInfo()

        map.addInputListener(inputListener)
        map.addTapListener(geoObjectTapListener)

        userLocation = mapKitN.createUserLocationLayer(mapWindow)
        if (requireActivity()
                .checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false

        }

        binding.apply {
            // Changing camera's zoom by controls on the map
            buttonMinus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }

            buttonPlus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }

            location.setOnClickListener {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        viewModel.currentFavoriteMapObject.observe(viewLifecycleOwner) {
            if (it != null) {
                createMapObject(it)
                map.move(
                    CameraPosition(
                        Point(it.latitude, it.longitude),
                        /* zoom = */ 15.0f,
                        /* azimuth = */ 0.0f,
                        /* tilt = */ 0.0f,
                    ),
                    START_ANIMATION,
                    null
                )
            } else {
                userLocation.setObjectListener(locationObjectListener)
            }
        }

        viewModel.flagShowAll.observe(viewLifecycleOwner) {
            if (it) {
                var aveLong = 0.0
                var aveLat = 0.0

                viewModel.allFavoriteMapObject.value?.onEach { dataMapObject ->
                    createMapObject(dataMapObject)
                    aveLong += dataMapObject.longitude
                    aveLat += dataMapObject.latitude
                }

                if (aveLat > 0.0) {
                    viewModel.allFavoriteMapObject.value?.let { list ->
                        aveLong /= list.size
                        aveLat /= list.size
                        map.move(
                            CameraPosition(
                                Point(aveLat, aveLong),
                                /* zoom = */ 13.0f,
                                /* azimuth = */ 0.0f,
                                /* tilt = */ 0.0f,
                            ),
                            START_ANIMATION,
                            null
                        )
                    }
                } else {
                    map.move(START_POSITION, START_ANIMATION, null)
                }
                viewModel.flagShowAll.value = false
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapKitN.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        mapKitN.onStop()
        super.onStop()
    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                SMOOTH_ANIMATION,
                null,
            )
        }
    }

    private fun updateFocusInfo() {
        val defaultPadding = resources.getDimension(R.dimen.default_focus_rect_padding)
        //   val bottomPadding = binding.layoutBottomCard.measuredHeight
        val rightPadding = binding.buttonMinus.measuredWidth
        val bottomEditPadding = binding.textZoom.measuredHeight
        // Focus rect consider a bottom card UI and map zoom controls.
        mapWindow.focusRect = ScreenRect(
            ScreenPoint(defaultPadding, defaultPadding),
            ScreenPoint(
                mapWindow.width() - rightPadding - defaultPadding,
                mapWindow.height() - defaultPadding - bottomEditPadding,
            )
        )
        mapWindow.focusPoint = ScreenPoint(
            mapWindow.width() / 2f,
            (mapWindow.height() + defaultPadding + bottomEditPadding) / 2f,
        )
    }

    companion object {
        private const val ZOOM_STEP = 1f

        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 2f)
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)

        private val START_POSITION = CameraPosition(Point(55.796127, 49.106414), 10f, 0f, 0f)

        @JvmStatic
        fun newInstance() = MapsFragment()
    }
}