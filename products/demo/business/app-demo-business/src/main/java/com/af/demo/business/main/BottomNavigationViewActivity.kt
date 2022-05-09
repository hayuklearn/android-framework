package com.af.demo.business.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.Navigation.findNavController
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator
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

    private lateinit var mainFragmentNavigator: MainFragmentNavigator

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        TranslucentStatusCompat.requestTranslucentStatus(this, true)
        setContentView(binding.root)

        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val provider: NavigatorProvider = host.navController.navigatorProvider
        mainFragmentNavigator = MainFragmentNavigator(this, host.childFragmentManager, host.id)
        provider.addNavigator(mainFragmentNavigator)

        val navDestinations: NavGraph = initNavGraph(provider, mainFragmentNavigator)
        host.navController.graph = navDestinations

        NavigationUI.setupWithNavController(binding.bottomNavigationView, host.navController)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            host.navController.navigate(menuItem.itemId)
            true
        }
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.bottomNavigationView).navigateUp()

    private fun initNavGraph(
        provider: NavigatorProvider,
        fragmentNavigator: MainFragmentNavigator
    ): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))

        // 用自定义的导航器来创建目的地

        val destinationHome: FragmentNavigator.Destination = fragmentNavigator.createDestination()
        destinationHome.id = R.id.homeFragment
        destinationHome.setClassName(HomeFragment::class.java.canonicalName)
        navGraph.addDestination(destinationHome)

        val destinationMine: FragmentNavigator.Destination = fragmentNavigator.createDestination()
        destinationMine.id = R.id.mineFragment
        destinationMine.setClassName(MineFragment::class.java.canonicalName)
        navGraph.addDestination(destinationMine)

        navGraph.setStartDestination(destinationHome.id)

        return navGraph
    }
}
