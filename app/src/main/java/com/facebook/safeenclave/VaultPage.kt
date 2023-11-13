package com.facebook.safeenclave

import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.safeenclave.databinding.VaultPageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class VaultPage : Fragment() {

    private var _binding: VaultPageBinding? = null
    //private lateinit var vaultsList: ArrayList<VaultCard>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = VaultPageBinding.inflate(inflater, container, false)
        return binding.root

    }

    var fileNames = ArrayList<String>()
    var filePaths = ArrayList<String>()

    var multiplePermissionId = 14
    var multiplePermissionNameList = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        arrayListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (checkMultiplePermission()) {
            doOperation()
        }

//        val names = arrayOf(
//            "photo1.jpg", "photo2.jpg", "photo3.jpg", "photo4.png", "interesting.mp4"
//        )
//
//        val paths = arrayOf(
//            "root/Android/com.safe_evclave/My files/photo1.jpg", "root/Android/com.safe_evclave/My files/13-tka.png", "root/Android/com.safe_evclave/My files/nude.jpg", "root/Android/com.safe_evclave/My files/naked-14.png", "root/Android/com.safe_evclave/My files/interesting.mp4"
//        )

        val files: ArrayList<FileStruct> = ArrayList<FileStruct>()

        for (i in fileNames.indices)
        {
            val file = FileStruct(fileNames[i], filePaths[i])
            files.add(file)
        }

        binding.filesList.adapter = FileElementAdapter(requireActivity(), files)

        binding.settings.setOnClickListener {
            //TODO: TOAST
        }

        binding.vaultPageBack.setOnClickListener {
            findNavController().navigate(R.id.action_vaultPage_to_MainPage)
        }
    }

    private fun doOperation() {
        Toast.makeText(
            requireContext(),
            "All Permission Granted Successfully!",
            Toast.LENGTH_LONG
        ).show()
        var path: String = arguments?.getString("vault_path") ?: ""
        val name: String = arguments?.getString("vault_name") ?: ""
        println("---- " + path)
        val directoryUri: Uri = Uri.parse(path)

        println("---- " + directoryUri.path)

        val realPath = "/storage/emulated/0/" + directoryUri.path?.drop(14) + "/" + name.replace(" ", "_")
        println("--- " + realPath)
        val dir = File(realPath ?: "")
        val tmpFiles = dir.listFiles()
        tmpFiles?.forEach { file ->
            println("File name: ${file.name} - ${file.absolutePath}")
            fileNames.add(file.name)
            filePaths.add(file.absolutePath)
        }
        
    }

    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permission)
            } else {
                println("PERMISSION GRANTED!!!!!")
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionNeeded.toTypedArray(),
                multiplePermissionId
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == multiplePermissionId) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }
                if (isGrant) {
                    doOperation()
                } else {
                    var someDenied = false
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                permission
                            )
                        ) {
                            if (ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    permission
                                ) == PackageManager.PERMISSION_DENIED
                            ) {
                                someDenied = true
                            }
                        }
                    }
                    if (someDenied) {
                        appSettingOpen(requireContext())
                    } else {
                        warningPermissionDialog(requireContext()) { _: DialogInterface, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE ->
                                    checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }
    }

    fun appSettingOpen(context: Context){
        Toast.makeText(
            context,
            "Go to Setting and Enable All Permission",
            Toast.LENGTH_LONG
        ).show()

        val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        settingIntent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(settingIntent)
    }

    fun warningPermissionDialog(context: Context,listener : DialogInterface.OnClickListener){
        MaterialAlertDialogBuilder(context)
            .setMessage("All Permission are Required for this app")
            .setCancelable(false)
            .setPositiveButton("Ok",listener)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}