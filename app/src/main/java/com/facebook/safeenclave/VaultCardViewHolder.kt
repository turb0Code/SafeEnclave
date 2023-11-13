package com.facebook.safeenclave

import androidx.recyclerview.widget.RecyclerView
import com.facebook.safeenclave.databinding.VaultCardBinding

class VaultCardViewHolder(
    private val vaultCardBinding: VaultCardBinding
) : RecyclerView.ViewHolder(vaultCardBinding.root) {

    fun bindVault(vault: VaultCard) {
        vaultCardBinding.textName.text = vault.name
        vaultCardBinding.textSize.text = vault.size
    }

}