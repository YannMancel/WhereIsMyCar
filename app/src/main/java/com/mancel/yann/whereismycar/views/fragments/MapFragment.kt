package com.mancel.yann.whereismycar.views.fragments

import androidx.fragment.app.activityViewModels
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.viewModels.SharedViewModel

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseFragment] subclass.
 */
class MapFragment : BaseFragment() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_map

    override fun doOnCreateView() = this.configureLocationEvents()

    // -- LiveData --

    private fun configureLocationEvents() {
        this._viewModel
            .getLocationState()
            .observe(this.viewLifecycleOwner) { locationState ->
                locationState?.let {
                    this.updateUIWithLocationEvents(it)
                }
            }
    }

    // -- Location events --

    private fun updateUIWithLocationEvents(state: LocationState) {
        when (state) {
            is LocationState.Success -> { /* Success */ }
            is LocationState.Failure -> { /* Failure */ }
        }
    }
}