package com.af.demo.jetpack.navigation.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.af.demo.jetpack.R
import com.af.demo.jetpack.databinding.ActivityBottomNavigationViewBinding
import com.af.lib.ktext.getStatusBarHeight
import com.af.lib.ktext.getToolbarHeight
import com.af.lib.ktext.signature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity Using BottomNavigationView & NavigationUI
 *
 * Created by hayukleung@gmail.com on 2021-09-10.
 */
class BottomNavigationViewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBottomNavigationViewBinding.inflate(layoutInflater) }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {

        // set fullscreen --------------------------------------------------------------------------
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //     window.setFlags(
        //         WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT,
        //         WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        //     )
        // } else {
        //     window.setFlags(
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN
        //     )
        // }
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //     val window: Window = window
        //     window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        //     window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //     window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //     window.statusBarColor = Color.TRANSPARENT
        //     window.navigationBarColor = Color.BLACK
        // }
        // set fullscreen --------------------------------------------------------------------------

        super.onCreate(savedInstanceState)

        // TranslucentStatusCompat.requestTranslucentStatus(this)
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

        binding.toolbar.layoutParams.height = getToolbarHeight() + getStatusBarHeight()
        binding.toolbar.requestLayout()

        // step 1
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_navigation_host) as NavHostFragment
        val navController = host.navController
        // step 3
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        CoroutineScope(Dispatchers.IO).launch {
            signature(packageName)
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        return findNavController(this, R.id.bottom_navigation_view).navigateUp()
    }
}
