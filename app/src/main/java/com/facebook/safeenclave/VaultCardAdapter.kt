package com.facebook.safeenclave

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.safeenclave.databinding.ManageVaultsBinding
import com.facebook.safeenclave.databinding.VaultCardBinding

//NAJNOWSZE
class VaultCardAdapter(private val vaults: ArrayList<VaultCard>) : RecyclerView.Adapter<VaultCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultCardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VaultCardBinding.inflate(from, parent, false)
        return VaultCardViewHolder(binding)
    }

    override fun getItemCount(): Int = vaultsList.size

    override fun onBindViewHolder(holder: VaultCardViewHolder, position: Int) {
        holder.bindVault(vaults[position])
    }
}