package si.um.feri.androidrups

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import si.um.feri.androidrups.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // Set and display the dropdown menu (spinner)
        val spinnerTheme = view.findViewById(R.id.themeDropdown) as Spinner
        val adapterTheme = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item,
            arrayOf(resources.getString(R.string.light_theme), resources.getString(R.string.dark_theme)))
        spinnerTheme.adapter = adapterTheme
        spinnerTheme.setSelection(app.getChosenTheme()!!.toInt())

        val spinnerUserSession = view.findViewById(R.id.userSessionDropdown) as Spinner
        val adapterUserSession = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item,
            arrayOf(resources.getString(R.string.session_remain), resources.getString(R.string.session_logout)))
        spinnerUserSession.adapter = adapterUserSession
        spinnerUserSession.setSelection(app.getChosenUserSession()!!.toInt())

        // Save button functionalities
        binding.buttonSaveTheme.setOnClickListener {
            app.setApplicationTheme(spinnerTheme.selectedItemPosition.toString())
        }

        binding.buttonSaveUserSession.setOnClickListener {
            app.setChosenUserSession(spinnerUserSession.selectedItemPosition.toString())
        }

        // Language display & change
        binding.editTextLanguage.setText(Locale.getDefault().displayLanguage)
        binding.editTextLanguage.isEnabled = false

        binding.buttonLanguage.setOnClickListener {
            requireActivity().startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

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

    override fun onResume() {
        super.onResume()
        binding.editTextLanguage.setText(Locale.getDefault().displayLanguage)
    }
}