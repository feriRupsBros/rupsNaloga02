package si.um.feri.androidrups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.osmdroid.util.GeoPoint
import si.um.feri.androidrups.data.DataAirPollution
import si.um.feri.androidrups.data.RequestAddAirPollution
import si.um.feri.androidrups.databinding.FragmentAdminBinding
import si.um.feri.androidrups.dialog.MapPickerFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // Time services
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        // Add preset data
        binding.editTextName.setText("")
        binding.editTextRegion.setText("")
        binding.editTextLatitude.setText("")
        binding.editTextLongitude.setText("")
        binding.editTextLatitude.isEnabled = false
        binding.editTextLongitude.isEnabled = false
        binding.editTextSource.setText(app.getUsername())
        binding.editTextSource.isEnabled = false
        binding.editTextReliability.setText("0.0")
        binding.editTextPM10.setText("0.0")
        binding.editTextPM25.setText("0.0")
        binding.editTextSO2.setText("0.0")
        binding.editTextCO.setText("0.0")
        binding.editTextO3.setText("0.0")
        binding.editTextNO2.setText("0.0")
        binding.editTextC6H6.setText("0.0")
        binding.editTextMeasuringStart.setText(currentDateTime.format(formatter))
        binding.editTextMeasuringEnd.setText(currentDateTime.format(formatter))

        binding.buttonSetLocation.setOnClickListener {
            val point: GeoPoint = try {
                GeoPoint(binding.editTextLatitude.text.toString().toDouble(), binding.editTextLongitude.text.toString().toDouble())
            }catch (e: Exception) {
                GeoPoint(.0,.0)
            }
            MapPickerFragment.startDialog(
                requireActivity(),
                viewLifecycleOwner,
                point
            ) {
                binding.editTextLatitude.setText("${it.latitude}")
                binding.editTextLongitude.setText("${it.longitude}")
            }
        }

        binding.buttonAdd.setOnClickListener {
            val latitude: Float
            val longitude: Float
            val reliability: Float
            val pm10: Float
            val pm25: Float
            val so2: Float
            val co: Float
            val o3: Float
            val no2: Float
            val c6h6: Float

            val name = binding.editTextName.text.toString()
            val region = binding.editTextRegion.text.toString()
            val source = binding.editTextSource.text.toString()
            val measuringStart = binding.editTextMeasuringStart.text.toString()
            val measuringEnd = binding.editTextMeasuringEnd.text.toString()

            if (name == "") {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_01),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener
            }
            if (region == "") {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_02),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener
            }
            try {
                latitude = binding.editTextLatitude.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_03),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                longitude = binding.editTextLongitude.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_04),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            if (source == "") {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_05),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener
            }
            try {
                reliability = binding.editTextReliability.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_06),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                pm10 = binding.editTextPM10.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_07),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                pm25 = binding.editTextPM25.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_08),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                so2 = binding.editTextSO2.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_09),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                co = binding.editTextCO.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_10),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                o3 = binding.editTextO3.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_11),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                no2 = binding.editTextNO2.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_12),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            try {
                c6h6 = binding.editTextLatitude.text.toString().toFloat()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_13),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            if (measuringStart == "") {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_14),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener
            }
            try {
                LocalDateTime.parse(measuringStart, formatter)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_15),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }
            if (measuringEnd == "") {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_16),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener
            }
            try {
                LocalDateTime.parse(measuringEnd, formatter)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.admin_add_error_17),
                    Toast.LENGTH_LONG
                ).show(); return@setOnClickListener; }

            val airPollution = RequestAddAirPollution(
                name = name,
                region = region,
                latitude = latitude,
                longtitude = longitude,
                source = source,
                reliability = reliability,
                PM10 = pm10,
                PM2_5 = pm25,
                SO2 = so2,
                CO = co,
                O3 = o3,
                NO2 = no2,
                C6H6 = c6h6,
                measuring_start = measuringStart,
                measuring_end = measuringEnd,
            )

            val res = app.addAirPollution(airPollution)
            if (res is DataAirPollution) {
                val bundle = Bundle()
                bundle.putSerializable("airPollution", res)
                findNavController().navigate(
                    R.id.action_adminFragment_to_singleLocationFragment,
                    bundle
                )
            }
        }

        // Home & Login/Logout functionalities
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_to_homeFragment)
        }

        binding.buttonLoginPage.setOnClickListener {
            findNavController().navigate(R.id.action_to_loginFragment)
        }

        if (app.userVerifyStatus()) {
            binding.buttonLoginPage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.logout
                )
            )
        } else {
            binding.buttonLoginPage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.login
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _app = null
    }
}