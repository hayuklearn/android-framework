package com.af.demo.business.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.af.demo.business.R
import com.af.demo.business.databinding.ActivityBottomNavigationViewBinding
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

        setContentView(binding.root)

        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val provider: NavigatorProvider = host.navController.navigatorProvider
        val navigator = BusinessFragmentNavigator(this, host.childFragmentManager, host.id)
        provider.addNavigator(navigator)
        host.navController.graph = initNavGraph(provider, navigator)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, host.navController)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            host.navController.navigate(item.itemId)
            true
        }
    }

    private fun initNavGraph(
        provider: NavigatorProvider,
        fragmentNavigator: BusinessFragmentNavigator
    ): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))

        // 用自定义的导航器来创建目的地

        val destinationHome: BusinessFragmentNavigator.Destination = fragmentNavigator.createDestination()
        destinationHome.id = R.id.homeFragment
        destinationHome.setClassName(HomeFragment::class.java.name)
        navGraph.addDestination(destinationHome)

        val destinationMine: BusinessFragmentNavigator.Destination = fragmentNavigator.createDestination()
        destinationMine.id = R.id.mineFragment
        destinationMine.setClassName(MineFragment::class.java.name)
        navGraph.addDestination(destinationMine)

        navGraph.setStartDestination(destinationHome.id)

        return navGraph
    }
}
