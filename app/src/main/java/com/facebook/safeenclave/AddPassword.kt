package com.facebook.safeenclave

import android.app.ApplicationExitInfo
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.safeenclave.databinding.AddPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class AddPassword : Fragment() {

    private var _binding: AddPasswordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = AddPasswordBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addVaultBtn.setOnClickListener {
            //OBTAIN INPUTS
            val passwordInput: EditText = view.findViewById(R.id.add_password)
            val confirmationInput: EditText = view.findViewById(R.id.add_confirmation)
            val password: String = passwordInput.text.toString()
            val confirmation: String = confirmationInput.text.toString()

            //CHECK IF PASSWORDS ARE SAME
            if (password != confirmation)
            {
                val passFieldLayout: TextInputLayout = view.findViewById(R.id.pass_field_layout)
                val confFieldLayout: TextInputLayout = view.findViewById(R.id.conf_field_layout)

                passFieldLayout.error = "Passwords are not the same"
                confFieldLayout.error = "Passwords are not the same"
            }
            else
            {
                //OBTAIN VARS FROM PREVIOUS PAGE
                val vaultName: String = arguments?.getString("vault_name")?:""
                val blackHole: Boolean = arguments?.getBoolean("black_hole")?:false
                val oncePassword: Boolean = arguments?.getBoolean("once_password")?:false
                val bioAuth: Boolean = arguments?.getBoolean("bio_auth")?:false
                var vaultPath: String = arguments?.getString("vault_path")?:""


                //CREATE SOME NEEDED VARS
                val security = Security()
                val hash = security.sha256(password)  //PASSWORD
                val gson = Gson()

                //JSON STUFF
                val vaultsPath = requireContext().filesDir.absolutePath + "/vaults.json"
                val lastPath = requireContext().filesDir.absolutePath + "/last.json"

                val vaultsFile = File(vaultsPath)
                val lastFile = File(lastPath)
                val vaultsFileEx = vaultsFile.exists()
                val lastFileEx = lastFile.exists()


                //ADD NEW VAULT TO MAIN JSON
                if (!vaultsFileEx) {
                    val vaults = hashMapOf<String, MainJsonStruct>(vaultPath to MainJsonStruct(vaultName, hash))
                    val json = gson.toJson(vaults)
                    vaultsFile.writeText(json)
                }
                else {
                    val json = vaultsFile.readText()
                    val type = object : TypeToken<HashMap<String, MainJsonStruct>>() {}.type
                    val vaults: HashMap<String, MainJsonStruct> = gson.fromJson(json, type)
                    vaults[vaultPath] = MainJsonStruct(vaultName, hash)
                    val newJson = gson.toJson(vaults)
                    vaultsFile.writeText(newJson)
                }

                if (lastFileEx) {
                    lastFile.delete()
                }

                //OVERRIDE JSON
                val last = hashMapOf<String, String>("last" to vaultPath)
                val json = gson.toJson(last)
                lastFile.writeText(json)

                //CREATE FOLDER
                val uri: Uri = Uri.parse(vaultPath)
                println("---- " + uri.path)
                val realPath = "/storage/emulated/0/" + uri.path?.drop(14)
                val name = vaultName.replace(" ", "_")
                val dir = File(realPath, name)
                println("----- " + dir)
                dir.mkdirs()
//                val treeUri = DocumentsContract.buildTreeDocumentUri(uri.authority, DocumentsContract.getTreeDocumentId(uri))
//                val parentDocumentFile = DocumentFile.fromTreeUri(requireContext(), treeUri)
//                val name = vaultName.replace(" ", "_")
//                val new = parentDocumentFile?.createDirectory(name)


                //NAV TO MAIN LOGIN
                findNavController().navigate(R.id.action_addPassword_to_MainPage)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}