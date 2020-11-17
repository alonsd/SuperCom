package com.supercom.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.supercom.databinding.ViewholderCountryBinding
import com.supercom.model.CountryCasesModel
import com.supercom.utils.adapter.DefaultAdapterDiffUtilCallback

class CountryAdapter(private val context: Context) : androidx.recyclerview.widget.ListAdapter<CountryCasesModel, CountryViewHolder>(DefaultAdapterDiffUtilCallback<CountryCasesModel>()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ViewholderCountryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CountryViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }
}