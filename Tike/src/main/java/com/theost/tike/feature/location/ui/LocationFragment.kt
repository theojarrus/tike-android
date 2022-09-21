package com.theost.tike.feature.location.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.changeText
import com.theost.tike.common.extension.fazy
import com.theost.tike.common.extension.pressBack
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.common.util.LocationUtils.hasLocationPermission
import com.theost.tike.common.util.LogUtils.LOG_FRAGMENT_LOCATION
import com.theost.tike.databinding.FragmentLocationBinding
import com.theost.tike.domain.model.core.Location
import com.theost.tike.feature.creation.adding.main.presentation.EventViewModel
import com.theost.tike.feature.location.presentation.LocationViewModel
import com.theost.tike.feature.location.ui.adapter.LocationsAdapter
import com.theost.tike.feature.location.ui.map.MapController
import com.theost.tike.feature.location.ui.map.MapGeocoder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.newThread
import java.util.concurrent.TimeUnit.MILLISECONDS


class LocationFragment : Fragment(R.layout.fragment_location) {

    private val permissionLauncher by fazy {
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                mapController.enableMyLocation()
            } else {
                showError(requireContext(), R.string.error_permission)
            }
        }
    }

    private val mapGeocoder by fazy { MapGeocoder(requireContext()) }
    private val mapController by fazy {
        MapController(requireContext(), childFragmentManager, R.id.map)
    }

    private val locationsAdapter by fazy { LocationsAdapter(requireContext()) }

    private val eventViewModel: EventViewModel by activityViewModels()
    private val viewModel: LocationViewModel by viewModels()
    private val args: LocationFragmentArgs by navArgs()
    private val binding: FragmentLocationBinding by viewBinding()

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { activity.pressBack() }

        binding.locationButton.setOnClickListener {
            eventViewModel.setLocation(viewModel.location.value)
            findNavController().navigateUp()
        }

        binding.locationInput.apply {
            setAdapter(locationsAdapter)
            setOnItemClickListener { _, _, position, _ ->
                selectLocation(locationsAdapter.getLocationItem(position))
            }
        }

        args.location?.also { location ->
            viewModel.init(location)
            binding.locationButton.isGone = true
            binding.locationInput.setText(location.address)
            binding.locationInput.isFocusable = false
            mapController.init(viewModel.location.value, false)
        } ?: let {
            viewModel.init(eventViewModel.location.value)
            mapController.init(viewModel.location.value, true)
            initMap()
            initAddress()
        }

        viewModel.location.value?.let { updateAddress(it.address) }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (hasLocationPermission(requireContext())) {
            mapController.enableMyLocation()
        } else {
            permissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun initAddress() {
        compositeDisposable.addAll(
            Observable.create { emitter ->
                binding.locationInput.apply {
                    addTextChangedListener {
                        if (hasFocus()) emitter.onNext(it.toString().trim())
                    }
                }
            }.distinctUntilChanged()
                .doOnNext { binding.locationButton.isEnabled = false }
                .debounce(500, MILLISECONDS)
                .switchMapSingle(mapGeocoder::getLocations)
                .observeOn(mainThread())
                .doOnNext { locations ->
                    if (locations.isNotEmpty()) {
                        mapController.updateLocation(
                            locations[0].latitude,
                            locations[0].longitude,
                            true
                        )
                    } else {
                        mapController.clearLocation()
                    }
                }
                .subscribe({ addresses ->
                    updateDropdown(addresses)
                }, { error ->
                    Log.e(LOG_FRAGMENT_LOCATION, error.toString())
                })
        )
    }

    private fun initMap() {
        compositeDisposable.addAll(
            Observable.create { emitter ->
                mapController.setMapListener { latitude, longitude ->
                    emitter.onNext(Pair(latitude, longitude))
                }
            }.distinctUntilChanged()
                .doOnNext { binding.locationButton.isEnabled = false }
                .observeOn(newThread())
                .switchMapSingle { mapGeocoder.getLocation(it.first, it.second) }
                .observeOn(mainThread())
                .subscribe({ location ->
                    viewModel.setLocation(location)
                    updateAddress(location.address)
                }, { error ->
                    Log.e(LOG_FRAGMENT_LOCATION, error.toString())
                })
        )
    }

    private fun selectLocation(location: Location) {
        viewModel.setLocation(location)
        mapController.updateLocation(location.latitude, location.longitude, true)
        binding.locationButton.isEnabled = true
    }

    private fun updateAddress(location: String) {
        binding.locationButton.isEnabled = true
        binding.locationInput.apply {
            locationsAdapter.clearItems()
            clearFocus()
            changeText(location)
        }
    }

    private fun updateDropdown(locations: List<Location>) {
        locationsAdapter.setItems(locations)
        if (locations.none { it.address == binding.locationInput.text.toString() }) binding.locationInput.showDropDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
