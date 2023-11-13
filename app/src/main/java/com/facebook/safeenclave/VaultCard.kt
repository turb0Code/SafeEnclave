package com.facebook.safeenclave

var vaultsList = ArrayList<VaultCard>()

data class VaultCard (
    val name: String,
    val size: String,
    val path: String,
    val id: Int? = vaultsList.size
)