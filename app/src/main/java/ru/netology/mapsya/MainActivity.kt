package ru.netology.mapsya

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.runtime.image.ImageProvider
import ru.netology.mapsya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var mapWindow: MapWindow
    private lateinit var map: Map
    private lateinit var mapView: MapView
 //   private lateinit var placeMark2:PlacemarkMapObject

    private lateinit var placemarkMapObject: PlacemarkMapObject

    private val sizeChangedListener = SizeChangedListener { _, _, _ ->
        // Recalculate FocusRect and FocusPoint on every map's size change
        updateFocusInfo()
    }

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            this@MainActivity,
            "Tapped the point (${point.longitude}, ${point.latitude})",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

//    private val mapListener = MapObjectTapListener {_, point ->
//        val imageProvider = ImageProvider.fromResource(this, R.drawable.pin_green)
//        placeMark2 = mapView.map.mapObjects.addPlacemark().apply {
//            geometry = Point(point.latitude, point.longitude)
//            setIcon(imageProvider)
//        }
//        true
//    }

    private val inputListener = object : InputListener {
        override fun onMapLongTap(map: Map, point: Point) {
            // Move placemark after long tap
            placemarkMapObject.geometry = point
            Toast.makeText(
                this@MainActivity,
                "Tapped onMapLong the point (${point.longitude}, ${point.latitude})",
                Toast.LENGTH_SHORT
            ).show()

        }

        override fun onMapTap(map: Map, point: Point) {

            Toast.makeText(
                this@MainActivity,
                "Tapped onMap the point (${point.longitude}, ${point.latitude})",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val geoObjectTapListener = GeoObjectTapListener {
        // Move camera to selected geoObject
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            map.move(position, SMOOTH_ANIMATION, null)
        }

        val selectionMetadata = it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        map.selectGeoObject(selectionMetadata)
        Toast.makeText(
            this@MainActivity,
            "Tapped ${it.geoObject.name} id = ${selectionMetadata.objectId}",
            Toast.LENGTH_SHORT
        ).show()

        true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapview)

        mapWindow = binding.mapview.mapWindow
        map = mapWindow.map

        mapWindow.addSizeChangedListener(sizeChangedListener)
        updateFocusInfo()
//        map.move(
//            CameraPosition(
//                Point(55.751225, 37.629540),
//                /* zoom = */ 17.0f,
//                /* azimuth = */ 150.0f,
//                /* tilt = */ 30.0f,
//
//            )
//        )

        map.addInputListener(inputListener)
        map.addTapListener(geoObjectTapListener)

        map.move(START_POSITION, START_ANIMATION, null)

        createPlacemark(START_POSITION.target)

        val polyline = Polyline(POINTS)
        map.mapObjects.addPolyline(polyline)

        val imageProvider = ImageProvider.fromResource(this, R.drawable.pin_green)
        val placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(59.935493, 30.327392)
            setIcon(imageProvider)
        }
        placemark.addTapListener(placemarkTapListener)

//        val collection = map.mapObjects.addCollection()
//
//        collection.addPlacemark {
//
//        }

      //  placeMark2.addTapListener(mapListener)
        binding.apply {
            // Changing camera's zoom by controls on the map
            buttonMinus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }
            buttonPlus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }

            buttonFocusGeometry.setOnClickListener {
                val geometry = Geometry.fromPolyline(polyline)
                val position = map.cameraPosition(geometry)
                map.move(position, SMOOTH_ANIMATION, null)
            }
//            buttonFocusPlacemark.setOnClickListener {
//                val position = map.cameraPosition.run {
//                    CameraPosition(placemarkMapObject.geometry, zoom, azimuth, tilt)
//                }
//                map.move(position, SMOOTH_ANIMATION, null)
//            }
            buttonCreatePlacemark.setOnClickListener {
                // Usage of the screen coordinates to display placemarks in the center of a screen.
                val focusPoint = mapWindow.focusPoint ?: return@setOnClickListener
                val point = mapWindow.screenToWorld(focusPoint) ?: return@setOnClickListener
                placemarkMapObject.geometry = point
            }
        }

    }

//    override fun onStart() {
//        super.onStart()
//        MapKitFactory.getInstance().onStart()
//        mapView.onStart()
//    }

//    override fun onStop() {
//        mapView.onStop()
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }

    private fun createPlacemark(point: Point) {
        placemarkMapObject = map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(this@MainActivity, R.drawable.pin_green),
            IconStyle().apply { anchor = PointF(0.5f, 1.0f) }
        ).apply {
            isDraggable = true
        }
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
        val bottomPadding = binding.layoutBottomCard.measuredHeight
        val rightPadding = binding.buttonMinus.measuredWidth
        // Focus rect consider a bottom card UI and map zoom controls.
        mapWindow.focusRect = ScreenRect(
            ScreenPoint(defaultPadding, defaultPadding),
            ScreenPoint(
                mapWindow.width() - rightPadding - defaultPadding,
                mapWindow.height() - bottomPadding - defaultPadding,
            )
        )
        mapWindow.focusPoint = ScreenPoint(
            mapWindow.width() / 2f,
            mapWindow.height() / 2f,
        )
    }

    companion object {
        private const val ZOOM_STEP = 1f

        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 1f)
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)

        private val START_POSITION = CameraPosition(Point(54.707590, 20.508898), 15f, 0f, 0f)

        private val POINTS = listOf(
            Point(54.701079, 20.513011),
            Point(54.702409, 20.505102),
            Point(54.709270, 20.508272),
            Point(54.708539, 20.514920),
            Point(54.705865, 20.514524),
            Point(54.706133, 20.511160),
        )
    }
}