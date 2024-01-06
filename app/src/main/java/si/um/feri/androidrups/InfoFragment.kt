package si.um.feri.androidrups

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import si.um.feri.androidrups.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private var _app: MyApplication? = null
    private val app get() = _app!!

    private val files: Array<String> = arrayOf(
        "https://www.arso.gov.si/zrak/kakovost%20zraka/poro%c4%8dila%20in%20publikacije/porocilo_2022_Merged.pdf",
        "https://www.arso.gov.si/zrak/kakovost%20zraka/poro%c4%8dila%20in%20publikacije/Letno_porocilo_2021_Final.pdf",
        "https://www.arso.gov.si/zrak/kakovost%20zraka/poro%c4%8dila%20in%20publikacije/Letno_Porocilo_2020_Final.pdf"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        _app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // Split paths to get filename
        val splitFiles: MutableList<Array<String>> = mutableListOf()
        for (file in files) {
            splitFiles.add(file.split("/").toTypedArray())
        }

        // Files 2022
        binding.textReport2022Name.text = splitFiles[0][splitFiles[0].size - 1]
        binding.buttonReport2022.setOnClickListener {
            val manager: DownloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri = Uri.parse(files[0])
            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,splitFiles[0][splitFiles[0].size - 1])
            manager.enqueue(request)
        }

        // Files 2021
        binding.textReport2021Name.text = splitFiles[1][splitFiles[1].size - 1]
        binding.buttonReport2021.setOnClickListener {
            val manager: DownloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri = Uri.parse(files[1])
            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,splitFiles[1][splitFiles[1].size - 1])
            manager.enqueue(request)
        }

        // Files 2020
        binding.textReport2020Name.text = splitFiles[2][splitFiles[2].size - 1]
        binding.buttonReport2020.setOnClickListener {
            val manager: DownloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri = Uri.parse(files[2])
            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,splitFiles[2][splitFiles[2].size - 1])
            manager.enqueue(request)
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

}