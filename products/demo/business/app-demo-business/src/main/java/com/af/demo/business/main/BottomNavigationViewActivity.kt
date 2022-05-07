package com.af.demo.business.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.af.demo.business.R
import com.af.demo.business.databinding.ActivityBottomNavigationViewBinding
import com.af.lib.utils.Android
import com.af.lib.utils.TranslucentStatusCompat

/**
 * Activity Using BottomNavigationView & NavigationUI
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class BottomNavigationViewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBottomNavigationViewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        TranslucentStatusCompat.requestTranslucentStatus(this, true)
        // StatusBar.requestStatusBarLightOnMarshmallow(window, true)

        setContentView(binding.root)

        // step 1
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_navigation_host) as NavHostFragment
        val navController = host.navController
        // step 3
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        Android.signature(this, packageName)
    }

    override fun onSupportNavigateUp(): Boolean {

        return findNavController(this, R.id.bottomNavigationView).navigateUp()
    }
}
