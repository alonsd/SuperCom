package com.supercom.view.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.supercom.R
import com.supercom.databinding.ViewholderCountryBinding
import com.supercom.model.CountryCasesModel

class CountryViewHolder(
    private val binding: ViewholderCountryBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: CountryCasesModel) {

        binding.viewholderCountryCountry.text = item.country
        binding.viewholderCountryCases.text = item.cases.toString()
            .plus(" ")
            .plus(context.getString(R.string.viewholder_country_new_cases))
    }
}