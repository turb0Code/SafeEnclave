package com.facebook.safeenclave

import com.facebook.safeenclave.databinding.ManageVaultsBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ManageVaults : Fragment() {

    private var _binding: ManageVaultsBinding? = null
    //private lateinit var vaultsList: ArrayList<VaultCard>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ManageVaultsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val names = arrayOf(
            "Secret Files", "My files", "Secure vault", "Super secure", "Ultra safe"
        )

        val sizes = arrayOf(
            "2.56GB", "1.79GB", "849MB", "1.35GB", "4.03GB"
        )

        val paths = arrayOf(
            "android/DCIM/Secret Files", "root/Android/com.safe_evclave/My files", "shared/apps/safe_enclave/Secure vault", "shared/DCIM/vaults/Super secure", "emulated/0/safe/vaults/Ultra safe"
        )

        for (i in names.indices)
        {
            val vault = VaultCard(names[i], sizes[i], paths[i])
            vaultsList.add(vault)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = VaultCardAdapter(vaultsList)
        }

//        binding.leftList.adapter = VaultCardAdapter(requireActivity(), vaultsList)
//        binding.rightList.adapter = VaultCardAdapter(requireActivity(), vaultsList)

        binding.addVault.setOnClickListener {
            findNavController().navigate(R.id.action_manageVaults_to_addVault)
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_manageVaults_to_MainPage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}