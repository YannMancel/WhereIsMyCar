package com.mancel.yann.whereismycar.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * An abstract [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    // FIELDS --------------------------------------------------------------------------------------

    protected lateinit var _rootView: View

    // METHODS -------------------------------------------------------------------------------------

    @LayoutRes
    protected abstract fun getFragmentLayout(): Int

    protected abstract fun doOnCreateView()

    // -- Fragment --

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this._rootView = inflater.inflate(this.getFragmentLayout(), container, false)

        this.doOnCreateView()

        return this._rootView
    }
}