package com.supercom.model

import com.supercom.utils.adapter.DefaultAdapterDiffUtilCallback

data class CountryCasesModel(
    val country: String,
    val cases: Int
) : DefaultAdapterDiffUtilCallback.ModelWithId {
    override fun fetchId(): String = country
}