package si.um.feri.androidrups.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import si.um.feri.androidrups.R
import si.um.feri.androidrups.databinding.FragmentMapPickerBinding
import timber.log.Timber

private const val ARG_POSITION = "ARG_POSITION"
private const val REQUEST_KEY = "GET_POSITION"
private const val TAG = "PositionPickerFragment"

class MapPickerFragment : DialogFragment() {
    private var _binding: FragmentMapPickerBinding? = null
    private val binding get() = _binding!!

    private var _map: MapView? = null
    private val map get() = _map!!

    private var _mapController: IMapController? = null
    private val mapController get() = _mapController!!

    private lateinit var position: GeoPoint

    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allPermissionsGranted = true
            for (permission in result.values) {
                allPermissionsGranted = allPermissionsGranted && permission
                Timber.d("Checked permission: $permission")
            }

            Timber.d("Permissions granted - $allPermissionsGranted")
            if (allPermissionsGranted) {
                initMap()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            position = it.getSerializable(ARG_POSITION) as GeoPoint
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))

        _map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        _mapController = map.controller


        // Permissions functionalities
        val appPerms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )
        activityResultLauncher.launch(appPerms)

        initMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val markerPrevious = Marker(map)
        markerPrevious.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerPrevious.icon = ContextCompat.getDrawable(requireContext(), R.drawable.normal_pin)
        markerPrevious.position.longitude = position.longitude
        markerPrevious.position.latitude = position.latitude
        map.overlays.add(markerPrevious)
        mapController.setZoom(15.0)
        mapController.setCenter(markerPrevious.position)

        var markerMe: Marker? = null
        fun getPositionMarker(): Marker {
            if (markerMe == null) {
                markerMe = Marker(map)
                markerMe!!.title = "Me" // getString(R.string.map_me)
                markerMe!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                markerMe!!.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.person_pin)
                map.overlays.add(markerMe)
            }
            return markerMe!!
        }

        fun updateLocation(loc: Location) {
            getPositionMarker().position.latitude = loc.latitude
            getPositionMarker().position.longitude = loc.longitude
            mapController.setZoom(15.0)
            mapController.setCenter(getPositionMarker().position)
        }

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                markerPrevious.position.latitude = p!!.latitude
                markerPrevious.position.longitude = p.longitude
                markerPrevious.icon = ContextCompat.getDrawable(requireContext(), R.drawable.push_pin)
                map.invalidate()
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }))
        // map.invalidate()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let { updateLocation(it) }
            }

        binding.setPosition.setOnClickListener {
            setFragmentResult(
                REQUEST_KEY, resultBundle(markerPrevious.position)
            )
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(position: GeoPoint) = MapPickerFragment().apply {
            arguments = newBundle(position)
        }

        private fun newBundle(position: GeoPoint) = Bundle().apply {
            putSerializable(ARG_POSITION, position)
        }

        fun resultBundle(position: GeoPoint) = Bundle().apply {
            putSerializable(ARG_POSITION, position)
        }

        fun startDialog(
            activity: FragmentActivity,
            viewLifecycleOwner: LifecycleOwner,
            position: GeoPoint,
            callback: (GeoPoint) -> Unit
        ) {
            val frag = newInstance(position)
            val supportFragmentManager = activity.supportFragmentManager
            // we have to implement setFragmentResultListener
            supportFragmentManager.setFragmentResultListener(
                REQUEST_KEY, viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == REQUEST_KEY) {
                    @Suppress("DEPRECATION")
                    val resPosition = bundle.getSerializable(ARG_POSITION) as GeoPoint
                    callback(resPosition)
                }
            }
            // show
            frag.show(supportFragmentManager, TAG)
        }
    }
}