package si.um.feri.androidrups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import si.um.feri.androidrups.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // General options
        binding.buttonGraphsPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_graphsFragment)
        }

        binding.buttonMapsPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
        }

        binding.buttonInfoPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_infoFragment)
        }

        binding.buttonLoginPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // Admin || User options
        if(app.userVerifyStatus()){
            binding.loginGroup.visibility = View.VISIBLE

            binding.buttonAdminPage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_adminFragment)
            }

            binding.buttonSettingsPage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }

            binding.buttonLoginPage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.logout))

        }
        else {
            binding.loginGroup.visibility = View.INVISIBLE

            binding.buttonLoginPage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.login))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _app = null
    }
}