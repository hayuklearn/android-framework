package com.af.demo.business.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

/**
 * =================================================================================================
 * __
 * / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 * / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 * / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 * /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-24
 */
@Navigator.Name("mainNavFragment")
class MainFragmentNavigator(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val mContainerId: Int
) : FragmentNavigator(
    mContext, mFragmentManager, mContainerId
) {
    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {

        if (mFragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        val frag = mFragmentManager.findFragmentByTag(className) ?: instantiateFragment(
            mContext,
            mFragmentManager,
            className,
            args
        )
        frag.arguments = args
        val ft = mFragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        // ft.replace(mContainerId, frag);
        val fragments = mFragmentManager.fragments
        for (fragment in fragments) {
            ft.hide(fragment!!)
        }
        if (!frag.isAdded) {
            ft.add(mContainerId, frag, className)
        }
        ft.show(frag)
        ft.setPrimaryNavigationFragment(frag)
        @IdRes val destId = destination.id

        // 通过反射获取 mBackStack
        val mBackStack: ArrayDeque<Int>
        try {
            val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            mBackStack = field[this] as ArrayDeque<Int>
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val initialNavigation = mBackStack.isEmpty()
        val isSingleTopReplacement = navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && mBackStack.peekLast() == destId
        val isAdded = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) {
            if (mBackStack.size > 1) {
                mBackStack.peekLast()?.let { last ->
                    mFragmentManager.popBackStack(generateBackStackName(mBackStack.size, last), FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
                }
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key, value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }

    val fragmentList: List<Fragment>
        get() = mFragmentManager.fragments

    companion object {
        private const val TAG = "MainFragmentNavigator"
    }
}