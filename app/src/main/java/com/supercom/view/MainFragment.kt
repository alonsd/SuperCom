package com.supercom.view

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.supercom.R
import com.supercom.databinding.FragmentMainBinding
import com.supercom.model.CountryCasesModel
import com.supercom.utils.getAnyDateAsISODate
import com.supercom.utils.getDate
import com.supercom.utils.getCurrentISODateAsString
import com.supercom.utils.getYesterdayISODateAsString
import com.supercom.utils.network.Resource
import com.supercom.view.adapter.CountryAdapter
import com.supercom.view.viewmodel.MainViewModel
import org.koin.android.ext.android.get
import java.util.*
import java.util.jar.Manifest

class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: CountryAdapter
    private lateinit var navController: NavController
    private var selectedFromDateAsLong: Long = Date().time - DateUtils.DAY_IN_MILLIS
    private var selectedToDateAsLong: Long = Date().time
    private var selectedFromDateAsString = ""
    private var selectedToDateAsString = ""
    private val mainViewModel = get<MainViewModel>()
    private val exampleCountries = listOf("germany", "italy", "greece")
    private val exampleCountriesList = mutableListOf<CountryCasesModel>()
    private val calendar = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        requestAppPermissions()
        initListeners()
        initRecycler()
        init(getYesterdayISODateAsString(), getCurrentISODateAsString())
    }

    private fun requestAppPermissions() {

        requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.BLUETOOTH), 10)
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION,) -> {
                // You can use the API that requires the permission.
            }
            else -> {
                // You can directly ask for the permission.

            }
        }
    }

    private fun initListeners() {
        binding.fragmentMainFromButton.setOnClickListener(this)
        binding.fragmentMainToButton.setOnClickListener(this)
    }

    private fun init(fromDate: String, toDate: String) {

        exampleCountries.forEach { country ->
            fetchCountries(country, fromDate, toDate)
        }
    }

    private fun initRecycler() {
        adapter = CountryAdapter(requireContext())
        binding.fragmentMainRecyclerview.setHasFixedSize(true)
        binding.fragmentMainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchCountries(countryName: String, fromDate: String, toDate: String) {
        mainViewModel.getCovidDeathsByCountryAndDate(countryName, fromDate, toDate).observe(viewLifecycleOwner, { resource ->
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

    override fun onClick(view: View?) {
        val currentYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        when (view?.id) {
            R.id.fragment_main_from_button -> {
                setSelectedDate(binding.fragmentMainFromDate, true, currentYear, month, day)
            }

            R.id.fragment_main_to_button -> {
                setSelectedDate(binding.fragmentMainToDate, false, currentYear, month, day)
            }
        }
    }

    private fun setSelectedDate(textView: TextView, isFromDate: Boolean, currentYear: Int, month: Int, day: Int) {
        exampleCountriesList.clear()
        val datePickerDialog = DatePickerDialog(requireContext(), { picker, year, monthOfYear, dayOfMonth ->
            if (isFromDate){
                selectedFromDateAsLong = picker.getDate().time
                selectedFromDateAsString = getAnyDateAsISODate(year, monthOfYear, dayOfMonth)
            } else {
                selectedToDateAsString = getAnyDateAsISODate(year, monthOfYear, dayOfMonth)
            }

            textView.text = dayOfMonth.toString()
                .plus(".")
                .plus(monthOfYear.toString())
                .plus(".")
                .plus(year.toString())

            init(selectedFromDateAsString, selectedToDateAsString)
        }, currentYear, month, day)
        if (isFromDate) {
            datePickerDialog.datePicker.maxDate = Date().time - DateUtils.DAY_IN_MILLIS

        } else {
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.datePicker.minDate = selectedFromDateAsLong + DateUtils.DAY_IN_MILLIS
        }
        datePickerDialog.show()
    }
}