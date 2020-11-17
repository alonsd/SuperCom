package com.supercom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supercom.R
import com.supercom.databinding.FragmentMainBinding
import com.supercom.model.CountryCasesModel
import com.supercom.utils.getNowISODateAsString
import com.supercom.utils.getYesterdayISODateAsString
import com.supercom.utils.network.Resource
import com.supercom.view.adapter.CountryAdapter
import com.supercom.view.viewmodel.MainViewModel
import org.koin.android.ext.android.get

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: CountryAdapter
    private val mainViewModel = get<MainViewModel>()
    private val exampleCountries = listOf("germany", "italy", "greece")
    private val exampleCountriesList = mutableListOf<CountryCasesModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initRecycler()
        exampleCountries.forEach { country ->
            fetchCountries(country)
        }
    }

    private fun initRecycler() {
        adapter = CountryAdapter(requireContext())
        binding.fragmentMainRecyclerview.setHasFixedSize(true)
        binding.fragmentMainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchCountries(countryName: String) {
        mainViewModel.getCovidDeathsByCountryAndDate(countryName, getYesterdayISODateAsString(), getNowISODateAsString()).observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.fragmentMainTopProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    handleSuccessfulFetch(resource)
                }
                is Resource.Exception -> {
                    Toast.makeText(requireContext(), resource.throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun handleSuccessfulFetch(resource: Resource<*>) {
        exampleCountriesList.add(resource.data as CountryCasesModel)
        if (exampleCountriesList.size != exampleCountries.size) return
        binding.fragmentMainTopProgressBar.visibility = View.GONE
        binding.fragmentMainRecyclerview.adapter = adapter
        adapter.submitList(exampleCountriesList)
    }
}