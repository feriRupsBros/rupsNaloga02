package si.um.feri.androidrups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import si.um.feri.androidrups.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        if(app.userVerifyStatus()){
            app.userLogout()
            findNavController().navigate(R.id.action_to_homeFragment)
        }

        binding.buttonLogin.setOnClickListener {
            if (app.userLogin(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString())){
                binding.editTextUsername.setText("")
                binding.editTextPassword.setText("")
                findNavController().navigate(R.id.action_to_homeFragment)
            }
        }

        binding.buttonRegisterPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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