package com.facebook.safeenclave

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.safeenclave.databinding.AddVaultBinding
import com.google.android.material.snackbar.Snackbar


class AddVault : Fragment() {

    private var vaultPath: String = ""
    private var vaultName: String = ""

    private var _binding: AddVaultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddVaultBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_addVault_to_manageVaults)
        }

        binding.pickFolder.setOnClickListener {
            pickFile()
        }

        binding.nextBtn.setOnClickListener {
            //GET ALL DATA
            val editText: EditText = view.findViewById(R.id.vault_name_field)
            val vaultName: String = editText.text.toString()
            val blackHole: Boolean = view.findViewById<CheckBox?>(R.id.black_hole).isChecked
            val oncePassword: Boolean = view.findViewById<CheckBox?>(R.id.once_password).isChecked
            val bioAuth: Boolean = view.findViewById<CheckBox?>(R.id.bio_auth).isChecked

            //CHECK IF FIELDS ARE EMPTY
            if (vaultName == "" || vaultPath == "")
            {
                Snackbar.make(view, "Fill all fields", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.errorTint))
                    .setTextColor(resources.getColor(R.color.errorText))
                    .show()
            }
            else
            {
                //SAVE DATA FOR NEXT PAGE
                val bundle = Bundle()
                bundle.putString("vault_name", vaultName)
                bundle.putBoolean("black_hole", blackHole)
                bundle.putBoolean("once_password", oncePassword)
                bundle.putBoolean("bio_auth", bioAuth)
                bundle.putString("vault_path", vaultPath)

                //NAV TO NEXT PAGE
                findNavController().navigate(R.id.action_addVault_to_addPassword, bundle)
            }
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        try {
            startActivityForResult(Intent.createChooser(intent, "Select vault directory"), 100)
        }
        catch (exception: Exception) {
            Toast.makeText(requireActivity(), "Please install file manager", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val uri: Uri? = data?.data
            uri?.let {
                vaultPath = uri.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}