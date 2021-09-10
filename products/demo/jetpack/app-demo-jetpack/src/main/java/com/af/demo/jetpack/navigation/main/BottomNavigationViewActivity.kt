package com.af.demo.jetpack.navigation.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.af.demo.jetpack.R
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController


/**
 * Activity Using BottomNavigationView & NavigationUI
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class BottomNavigationViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_view)

        // step 1
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_navigation_host) as NavHostFragment
        val navController = host.navController
        // step 2
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        // step 3
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.bottom_navigation_view).navigateUp()
    }
}
