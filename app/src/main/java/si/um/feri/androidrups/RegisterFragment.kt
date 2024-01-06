package si.um.feri.androidrups

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import si.um.feri.androidrups.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        binding.buttonRegister.setOnClickListener {
            if(app.userRegister(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString(), binding.editTextPasswordRepeat.text.toString())){
                binding.editTextUsername.setText("")
                binding.editTextPassword.setText("")
                binding.editTextPasswordRepeat.setText("")
                findNavController().navigate(R.id.action_to_homeFragment)
            }
        }

        // Home & Login/Logout functionalities
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_to_homeFragment)
        }

        binding.buttonLoginPage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.lock))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _app = null
    }
}