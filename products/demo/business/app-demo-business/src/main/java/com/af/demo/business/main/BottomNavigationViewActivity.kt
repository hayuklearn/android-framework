package com.af.demo.business.main


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.af.demo.business.R
import com.af.demo.business.databinding.ActivityBottomNavigationViewBinding
import com.af.lib.utils.Android

/**
 * Activity Using BottomNavigationView & NavigationUI
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class BottomNavigationViewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBottomNavigationViewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val controller = ViewCompat.getWindowInsetsController(binding.root)
        // 显示状态栏
        controller?.show(WindowInsetsCompat.Type.statusBars())
        // 隐藏状态栏
        // controller?.hide(WindowInsetsCompat.Type.statusBars())

        // 状态栏文字颜色改为黑色
        controller?.isAppearanceLightStatusBars = true

        // 状态栏文字颜色改为白色
        // controller?.isAppearanceLightStatusBars = false

        binding.toolbar.layoutParams.height = Android.getToolbarHeight(this) + Android.getStatusBarHeight(this)
        binding.toolbar.requestLayout()

        // step 1
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_navigation_host) as NavHostFragment
        val navController = host.navController
        // step 3
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        Android.signature(this, packageName)
    }

    override fun onSupportNavigateUp(): Boolean {

        return findNavController(this, R.id.bottom_navigation_view).navigateUp()
    }
}
