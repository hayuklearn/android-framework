package com.af.demo.jetpack.navigation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.navigation.Navigation.findNavController
import com.af.lib.utils.Android
import com.af.lib.utils.TranslucentStatusCompat
import android.annotation.SuppressLint
import android.graphics.Color


import android.view.WindowManager

import android.os.Build
import android.view.View
import android.view.Window
import com.af.demo.jetpack.R


/**
 * Activity Using BottomNavigationView & NavigationUI
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class BottomNavigationViewActivity : AppCompatActivity() {

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {

        // set fullscreen --------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            )
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.BLACK
        }
        // set fullscreen --------------------------------------------------------------------------

        super.onCreate(savedInstanceState)

        TranslucentStatusCompat.requestTranslucentStatus(this)
        setContentView(R.layout.activity_bottom_navigation_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.layoutParams.height = Android.getToolbarHeight(this) + Android.getStatusBarHeight(this)
        toolbar.requestLayout()

        // step 1
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_navigation_host) as NavHostFragment
        val navController = host.navController
        // step 2
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        // step 3
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        Android.signature(this, packageName)

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.bottom_navigation_view).navigateUp()
    }
}
