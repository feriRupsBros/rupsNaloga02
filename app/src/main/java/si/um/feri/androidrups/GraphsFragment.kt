package si.um.feri.androidrups

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import si.um.feri.androidrups.databinding.FragmentGraphsBinding

class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    // AndroidChart
    private lateinit var barChart: BarChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        val textColor = resources.getColor(typedValue.resourceId)

        // AndroidChart
        barChart = view.findViewById(R.id.idBarChart)
        app.airPollutionData.observe(requireActivity()) {
            val barPM10 = ArrayList<BarEntry>()
            val barPM25 = ArrayList<BarEntry>()
            val barSO2 = ArrayList<BarEntry>()
            val barCO = ArrayList<BarEntry>()
            val barO3 = ArrayList<BarEntry>()
            val barNO2 = ArrayList<BarEntry>()
            val barC6H6 = ArrayList<BarEntry>()
            val regions = ArrayList<String>()

            var i = 0f
            for (el in it) {
                barPM10.add(BarEntry(i, el.concentrations.PM10))
                barPM25.add(BarEntry(i, el.concentrations.PM2_5))
                barSO2.add(BarEntry(i, el.concentrations.SO2))
                barCO.add(BarEntry(i, el.concentrations.CO))
                barO3.add(BarEntry(i, el.concentrations.O3))
                barNO2.add(BarEntry(i, el.concentrations.NO2))
                barC6H6.add(BarEntry(i, el.concentrations.C6H6))
                regions.add(el.name)

                i += 1
            }

            val setPM10 = BarDataSet(barPM10, "PM10")
            val setPM25 = BarDataSet(barPM25, "PM2_5")
            val setSO2 = BarDataSet(barSO2, "SO2")
            val setCO = BarDataSet(barCO, "CO")
            val setO3 = BarDataSet(barO3, "O3")
            val setNO2 = BarDataSet(barNO2, "No2")
            val setC6H6 = BarDataSet(barC6H6, "C6H6")

            setPM10.color = Color.RED
            setPM25.color = Color.GREEN
            setSO2.color = Color.BLUE
            setCO.color = Color.MAGENTA
            setO3.color = Color.CYAN
            setNO2.color = Color.YELLOW
            setC6H6.color = Color.DKGRAY

            // Set bar data
            barChart.data = BarData(setPM10, setPM25, setSO2, setCO, setO3, setNO2, setNO2, setC6H6)

            // Force apply
            barChart.invalidate()

            // Graph settings
            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(regions)
            xAxis.labelRotationAngle = -45f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.setDrawGridLines(false)
            // xAxis.granularity = 1f
            // xAxis.setLabelCount(regions.size, true)
            barChart.xAxis.granularity = 1f
            barChart.setVisibleXRangeMaximum(regions.size.toFloat())
            xAxis.textColor = textColor
            xAxis.setCenterAxisLabels(true)

            barChart.axisLeft.axisMinimum = 0f
            barChart.axisLeft.setDrawGridLines(false)

            barChart.axisRight.axisMinimum = 0f
            barChart.axisRight.setDrawGridLines(false)
        }
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.legend.isEnabled = true
        barChart.legend.textColor = textColor

        // Group bars by city
        val groupSpace = 0.5f
        val barSpace = 0.05f
        val barWidth = 0.1f
        barChart.barData.barWidth = barWidth
        barChart.groupBars(0f, groupSpace, barSpace)

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
}