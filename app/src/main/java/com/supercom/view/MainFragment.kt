package com.supercom.view

import android.Manifest.permission.*
import android.app.DatePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
import com.supercom.utils.Constants.RequestCodes.LOCATION_REQUEST_CODE
import com.supercom.utils.getAnyDateAsISODate
import com.supercom.utils.getCurrentISODateAsString
import com.supercom.utils.getDate
import com.supercom.utils.getYesterdayISODateAsString
import com.supercom.utils.network.Resource
import com.supercom.utils.view.GeneralViewUtils
import com.supercom.view.adapter.CountryAdapter
import com.supercom.view.viewmodel.MainViewModel
import org.koin.android.ext.android.get
import java.util.*


class MainFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: CountryAdapter
    private lateinit var navController: NavController
    private var selfCountryData = ""
    private var selectedFromDateAsLong: Long = Date().time - DateUtils.DAY_IN_MILLIS
    private var selectedFromDateAsString = ""
    private var selectedToDateAsString = ""
    private val mainViewModel = get<MainViewModel>()
    private val exampleCountries = listOf("germany", "italy", "greece")
    private val exampleCountriesList = mutableListOf<CountryCasesModel>()
    private val calendar = Calendar.getInstance()


    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        requestAppPermissions()
        initListeners()
        initRecycler()
        initBluetooth()
    }

    private fun initBluetooth() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        requireActivity().registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action == BluetoothDevice.ACTION_FOUND) {
                    val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                    Log.d("devicesFound", device.toString())
                }
            }
        }, filter)
        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter.startDiscovery()
    }


    private fun requestAppPermissions() {

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION),
            ContextCompat.checkSelfPermission(requireContext(), BLUETOOTH_ADMIN) -> {
                getCountry()
            }
            else -> {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION, BLUETOOTH_ADMIN), LOCATION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != LOCATION_REQUEST_CODE) return
        getCountry()
    }

    private fun getCountry() {

        val locationManager: LocationManager
        try {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 5f) { location ->
                GeneralViewUtils.showFirstHideRest(binding.fragmentMainSelfCountryViewGroup, binding.fragmentMainCountryFetchProgressBar)
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                fetchCountry(addresses[0].countryCode, getYesterdayISODateAsString(), getCurrentISODateAsString()) { resource ->
                    handleSelfCountryDataFetch(resource)
                }
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 5f) { location ->
                GeneralViewUtils.showFirstHideRest(binding.fragmentMainSelfCountryViewGroup, binding.fragmentMainCountryFetchProgressBar)
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                fetchCountry(addresses[0].countryCode, getYesterdayISODateAsString(), getCurrentISODateAsString()) { resource ->
                    handleSelfCountryDataFetch(resource)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun handleSelfCountryDataFetch(resource: Resource<*>) {
        val countryCasesModel = resource.data as CountryCasesModel
        selfCountryData = getString(R.string.main_fragment_country)
            .plus(countryCasesModel.country)
            .plus(getString(R.string.comma))
            .plus(" ")
            .plus(getString(R.string.main_fragment_cases))
            .plus(" ")
            .plus(countryCasesModel.cases.toString())
    }

    private fun initListeners() {
        binding.fragmentMainFromButton.setOnClickListener(this)
        binding.fragmentMainToButton.setOnClickListener(this)
        binding.fragmentMainFetchCountryButton.setOnClickListener(this)
    }

    private fun fetchMainRecycler(fromDate: String, toDate: String) {

        exampleCountries.forEach { country ->
            fetchCountry(country, fromDate, toDate) { resource ->
                handleSuccessfulExampleCountriesFetch(resource)
            }
        }
    }

    private fun initRecycler() {
        adapter = CountryAdapter(requireContext())
        binding.fragmentMainRecyclerview.setHasFixedSize(true)
        binding.fragmentMainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        fetchMainRecycler(getYesterdayISODateAsString(), getCurrentISODateAsString())
    }

    private fun fetchCountry(countryName: String, fromDate: String, toDate: String, success: (resource: Resource<*>) -> Unit) {
        mainViewModel.getCovidDeathsByCountryAndDate(countryName, fromDate, toDate).observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    success(resource)
                }
                is Resource.Exception -> {
                    Toast.makeText(requireContext(), resource.throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun handleSuccessfulExampleCountriesFetch(resource: Resource<*>) {
        exampleCountriesList.add(resource.data as CountryCasesModel)
        if (exampleCountriesList.size != exampleCountries.size) return
        GeneralViewUtils.hideFirstShowRest(binding.fragmentMainTopProgressBar, binding.fragmentMainRecyclerview)
        binding.fragmentMainRecyclerview.adapter = adapter
        adapter.submitList(exampleCountriesList)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragment_main_from_button -> {
                openDateSelectorDialog(binding.fragmentMainFromDate, true)
            }

            R.id.fragment_main_to_button -> {
                openDateSelectorDialog(binding.fragmentMainToDate, false)
            }
            R.id.fragment_main_fetch_country_button -> {
                binding.fragmentMainFetchedCountryData.text = selfCountryData
            }
        }
    }

    private fun openDateSelectorDialog(textViewToDisplayResult: TextView, isFromDate: Boolean) {
        val currentYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { picker, year, monthOfYear, dayOfMonth ->
            handleOnDateSet(isFromDate, picker, year, monthOfYear, dayOfMonth, textViewToDisplayResult)
        }, currentYear, month, day)
        if (isFromDate) {
            datePickerDialog.datePicker.maxDate = Date().time - DateUtils.DAY_IN_MILLIS

        } else {
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.datePicker.minDate = selectedFromDateAsLong + DateUtils.DAY_IN_MILLIS
        }
        datePickerDialog.show()
    }

    private fun handleOnDateSet(isFromDate: Boolean, picker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int, textView: TextView) {
        exampleCountriesList.clear()
        if (isFromDate) {
            selectedFromDateAsLong = picker.getDate().time
            selectedFromDateAsString = getAnyDateAsISODate(year, monthOfYear, dayOfMonth)
        } else {
            selectedToDateAsString = getAnyDateAsISODate(year, monthOfYear, dayOfMonth)
        }

        textView.text = dayOfMonth.toString()
            .plus(getString(R.string.dot))
            .plus(monthOfYear.toString())
            .plus(getString(R.string.dot))
            .plus(year.toString())

        fetchMainRecycler(selectedFromDateAsString, selectedToDateAsString)
    }
}