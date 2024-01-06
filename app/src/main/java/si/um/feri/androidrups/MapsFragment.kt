package si.um.feri.androidrups

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import si.um.feri.androidrups.data.DataAirPollution
import si.um.feri.androidrups.databinding.FragmentMapsBinding
import timber.log.Timber

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    private var _map: MapView? = null
    private val map get() = _map!!

    private var _mapController: IMapController? = null
    private val mapController get() = _mapController!!

    private var latestSelected: Marker? = null
    private var airPollution: DataAirPollution? = null

    // Check for required application permissions
    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {result ->
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))

        _map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        _mapController = map.controller

        // More info functionalities
        binding.buttonMoreInfo.visibility = View.INVISIBLE
        binding.buttonMoreInfo.setOnClickListener {
            if(latestSelected != null) {
                val bundle = Bundle()
                bundle.putSerializable("airPollution", airPollution)
                findNavController().navigate(R.id.action_mapsFragment_to_singleLocationFragment, bundle)
            }
        }

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

        // Home & Login/Logout functionalities
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_to_homeFragment)
        }

        binding.buttonLoginPage.setOnClickListener {
            findNavController().navigate(R.id.action_to_loginFragment)
        }

        if(app.userVerifyStatus()){
            binding.buttonLoginPage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.logout))
        }
        else {
            binding.buttonLoginPage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.login))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _app = null
    }

    private fun initMap() {
        var marker: Marker? = null

        fun getPositionMarker(): Marker {
            if (marker == null) {
                marker = Marker(map)
                marker!!.title = resources.getString(R.string.current_location)
                marker!!.icon = ContextCompat.getDrawable(requireContext(), R.drawable.person_pin)
                marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                map.overlays.add(marker)
            }

            return marker!!
        }

        fun updateLocation(loc: Location) {
            getPositionMarker().position.latitude = loc.latitude
            getPositionMarker().position.longitude = loc.longitude
            mapController.setZoom(15.0)
            mapController.setCenter(getPositionMarker().position)
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location ->
            updateLocation(location)
        }


        app.airPollutionData.observe(requireActivity()) { airPollutions ->
            map.overlays.clear()
            map.invalidate()

            getPositionMarker()
            for (el in airPollutions) {
                val iconMarker = Marker(map)
                iconMarker.title = el.name
                iconMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.normal_pin)
                iconMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                iconMarker.position.latitude = el.geo.coordinates[1].toDouble()
                iconMarker.position.longitude = el.geo.coordinates[0].toDouble()
                iconMarker.snippet = "${el.region} (${el.source})"

                iconMarker.setOnMarkerClickListener { marker, _ ->
                    if(latestSelected != null){
                        latestSelected!!.icon = ContextCompat.getDrawable(requireContext(), R.drawable.normal_pin)
                    }
                    latestSelected = marker
                    airPollution = el

                    marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.push_pin)
                    map.invalidate()
                    binding.buttonMoreInfo.visibility = View.VISIBLE

                    marker.showInfoWindow()

                    return@setOnMarkerClickListener true
                }

                map.overlays.add(iconMarker)
            }
        }
    }
}