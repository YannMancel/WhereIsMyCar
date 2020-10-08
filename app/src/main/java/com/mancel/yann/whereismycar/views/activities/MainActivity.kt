package com.mancel.yann.whereismycar.views.activities

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.helpers.FullScreenHelper
import com.mancel.yann.whereismycar.views.fragments.MapFragment

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseActivity] subclass.
 */
class MainActivity : BaseActivity() {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _navController: NavController
    private lateinit var _appBarConfiguration: AppBarConfiguration

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseActivity --

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun doOnCreate() = this.configureActionBarForNavigation()

    // -- Activity --

    override fun onSupportNavigateUp(): Boolean {
        return this._navController.navigateUp(this._appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) FullScreenHelper.hideSystemUI(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragment =
            this.supportFragmentManager
                .findFragmentById(R.id.activity_main_NavHostFragment)!!
                .childFragmentManager
                .fragments[0]

        // Call the onActivityResult method of current Fragment
        // To check settings to location
        if (fragment is MapFragment) fragment.onActivityResult(requestCode, resultCode, data)
    }

    // -- Action bar --

    private fun configureActionBarForNavigation() {
        // NavController
        this._navController = this.findNavController(R.id.activity_main_NavHostFragment)

        // AppBarConfiguration
        this._appBarConfiguration = AppBarConfiguration(this._navController.graph)

        // Action bar
        this.setupActionBarWithNavController(this._navController, this._appBarConfiguration)
    }
}