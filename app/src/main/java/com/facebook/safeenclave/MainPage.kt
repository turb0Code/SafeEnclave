package com.facebook.safeenclave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.safeenclave.databinding.MainPageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainPage : Fragment() {
    private var _binding: MainPageBinding? = null
    private var lastVaultPath: String = ""
    private lateinit var vaultsMap: HashMap<String, MainJsonStruct>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = MainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gson = Gson()

        val vaultName: TextView = view.findViewById(R.id.vault_name)
        val vaultsPath = requireContext().filesDir.absolutePath + "/vaults.json"
        val lastPath = requireContext().filesDir.absolutePath + "/last.json"
        val vaultsFile = File(vaultsPath)
        val lastFile = File(lastPath)
        val vaultsFileExists = vaultsFile.exists()
        val lastFileExists = lastFile.exists()
        var lastVaultName = ""


        if (!vaultsFileExists || !lastFileExists) {
            findNavController().navigate(R.id.action_MainPage_to_addVault)
        }
        else {
            val lastJson = lastFile.readText()
            val type = object : TypeToken<HashMap<String, String>>() {}.type
            val lastMap: HashMap<String, String> = gson.fromJson(lastJson, type)
            val vaultsJson = vaultsFile.readText()
            val secondType = object : TypeToken<HashMap<String, MainJsonStruct>>() {}.type
            vaultsMap = gson.fromJson(vaultsJson, secondType)
            lastVaultPath = lastMap.get("last") ?: ""
            lastVaultName = vaultsMap.get(lastVaultPath)?.name ?: "NO NAME"
            vaultName.text = lastVaultName
        }

        binding.unlock.setOnClickListener {
            val passwordInput: EditText = view.findViewById(R.id.pass_field_layout)
            val security = Security()
            val password: String = security.sha256(passwordInput.text.toString())
            val hash: String = vaultsMap.get(lastVaultPath)?.pass ?: ""
            if (password != hash)
            {
                Toast.makeText(requireContext(), "WRONG PASS", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val lastJson = lastFile.readText()
                val type = object : TypeToken<HashMap<String, String>>() {}.type
                val lastMap: HashMap<String, String> = gson.fromJson(lastJson, type)
                lastVaultPath = lastMap.get("last") ?: ""
                val bundle: Bundle = Bundle()
                bundle.putString("vault_path", lastVaultPath)
                bundle.putString("vault_name", lastVaultName)
                println("--- " + lastVaultPath)
                findNavController().navigate(R.id.action_MainPage_to_vaultPage, bundle)
            }
        }

        binding.manage.setOnClickListener {
            findNavController().navigate(R.id.action_MainPage_to_manageVaults)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}