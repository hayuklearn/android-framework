package com.af.demo.business.main

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-05-11
 */
@Navigator.Name("businessFragmentNavigator")
class BusinessFragmentNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : Navigator<BusinessFragmentNavigator.Destination>() {
    private val savedIds = mutableSetOf<String>()

    /**
     * {@inheritDoc}
     *
     * This method must call `FragmentTransaction.setPrimaryNavigationFragment`
     * if the pop succeeded so that the newly visible Fragment can be retrieved with
     * [FragmentManager.getPrimaryNavigationFragment].
     *
     * Note that the default implementation pops the Fragment
     * asynchronously, so the newly visible Fragment from the back stack
     * is not instantly available after this call completes.
     */
    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        if (fragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state"
            )
            return
        }
        if (savedState) {
            val beforePopList = state.backStack.value
            val initialEntry = beforePopList.first()
            // Get the set of entries that are going to be popped
            val poppedList = beforePopList.subList(
                beforePopList.indexOf(popUpTo),
                beforePopList.size
            )
            // Now go through the list in reversed order (i.e., started from the most added)
            // and save the back stack state of each.
            for (entry in poppedList.reversed()) {
                if (entry == initialEntry) {
                    Log.i(
                        TAG,
                        "FragmentManager cannot save the state of the initial destination $entry"
                    )
                } else {
                    fragmentManager.saveBackStack(entry.id)
                    savedIds += entry.id
                }
            }
        } else {
            fragmentManager.popBackStack(
                popUpTo.id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        state.pop(popUpTo, savedState)
    }

    override fun createDestination(): Destination {
        return Destination(this)
    }

    /**
     * Instantiates the Fragment via the FragmentManager's
     * [androidx.fragment.app.FragmentFactory].
     *
     * Note that this method is **not** responsible for calling
     * [Fragment.setArguments] on the returned Fragment instance.
     *
     * @param context Context providing the correct [ClassLoader]
     * @param fragmentManager FragmentManager the Fragment will be added to
     * @param className The Fragment to instantiate
     * @return A new fragment instance.
     */
    private fun instantiateFragment(
        context: Context,
        fragmentManager: FragmentManager,
        className: String
    ): Fragment {
        return fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
    }

    /**
     * {@inheritDoc}
     *
     * This method should always call `FragmentTransaction.setPrimaryNavigationFragment`
     * so that the Fragment associated with the new destination can be retrieved with
     * [FragmentManager.getPrimaryNavigationFragment].
     *
     * Note that the default implementation commits the new Fragment
     * asynchronously, so the new Fragment is not instantly available
     * after this call completes.
     */
    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        if (fragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already saved its state"
            )
            return
        }
        for (entry in entries) {
            navigate(entry, navOptions, navigatorExtras)
        }
    }

    private fun navigate(
        entry: NavBackStackEntry,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        val backStack = state.backStack.value
        val initialNavigation = backStack.isEmpty()
        val restoreState = (
                navOptions != null && !initialNavigation &&
                        navOptions.shouldRestoreState() &&
                        savedIds.remove(entry.id)
                )
        if (restoreState) {
            // Restore back stack does all the work to restore the entry
            fragmentManager.restoreBackStack(entry.id)
            state.push(entry)
            return
        }
        val destination = entry.destination as Destination
        val args = entry.arguments
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        // val frag = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
        // frag.arguments = args
        // val ft = fragmentManager.beginTransaction()

        var frag = fragmentManager.findFragmentByTag(className)
        if (null == frag) {
            // ?????????????????????
            frag = instantiateFragment(context, fragmentManager, className)
        }
        frag.arguments = args
        val ft: FragmentTransaction = fragmentManager.beginTransaction()

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

        // ft.replace(containerId, frag)

        fragmentManager.fragments.forEach { fragment ->
            ft.hide(fragment)
        }
        if (!frag.isAdded) {
            ft.add(containerId, frag, className)
        }
        ft.show(frag)

        ft.setPrimaryNavigationFragment(frag)
        @IdRes val destId = destination.id
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (
                navOptions != null && !initialNavigation &&
                        navOptions.shouldLaunchSingleTop() &&
                        backStack.last().destination.id == destId
                )
        val isAdded = when {
            initialNavigation -> {
                true
            }
            isSingleTopReplacement -> {
                // Single Top means we only want one instance on the back stack
                if (backStack.size > 1) {
                    // If the Fragment to be replaced is on the FragmentManager's
                    // back stack, a simple replace() isn't enough so we
                    // remove it from the back stack and put our replacement
                    // on the back stack in its place
                    fragmentManager.popBackStack(
                        entry.id,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ft.addToBackStack(entry.id)
                }
                false
            }
            else -> {
                ft.addToBackStack(entry.id)
                true
            }
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key, value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        if (isAdded) {
            state.push(entry)
        }
    }

    override fun onSaveState(): Bundle? {
        if (savedIds.isEmpty()) {
            return null
        }
        return bundleOf(KEY_SAVED_IDS to ArrayList(savedIds))
    }

    override fun onRestoreState(savedState: Bundle) {
        val savedIds = savedState.getStringArrayList(KEY_SAVED_IDS)
        if (savedIds != null) {
            this.savedIds.clear()
            this.savedIds += savedIds
        }
    }

    /**
     * NavDestination specific to [FragmentNavigator]
     *
     * Construct a new fragment destination. This destination is not valid until you set the
     * Fragment via [setClassName].
     *
     * @param fragmentNavigator The [FragmentNavigator] which this destination will be associated
     * with. Generally retrieved via a [NavController]'s [NavigatorProvider.getNavigator] method.
     */
    @NavDestination.ClassType(Fragment::class)
    class Destination
    constructor(fragmentNavigator: Navigator<out Destination>) :
        NavDestination(fragmentNavigator) {

        /**
         * Construct a new fragment destination. This destination is not valid until you set the
         * Fragment via [setClassName].
         *
         * @param navigatorProvider The [NavController] which this destination
         * will be associated with.
         */
        constructor(navigatorProvider: NavigatorProvider) :
                this(navigatorProvider.getNavigator(BusinessFragmentNavigator::class.java))

        @CallSuper
        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            setClassName(className)
        }

        /**
         * Set the Fragment class name associated with this destination
         * @param className The class name of the Fragment to show when you navigate to this
         * destination
         * @return this [Destination]
         */
        fun setClassName(className: String): Destination {
            _className = className
            return this
        }

        private var _className: String? = null
        /**
         * The Fragment's class name associated with this destination
         *
         * @throws IllegalStateException when no Fragment class was set.
         */
        val className: String
            get() {
                checkNotNull(_className) { "Fragment class was not set" }
                return _className as String
            }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append(super.toString())
            sb.append(" class=")
            if (_className == null) {
                sb.append("null")
            } else {
                sb.append(_className)
            }
            return sb.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Destination) return false
            return super.equals(other) && _className == other._className
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + _className.hashCode()
            return result
        }
    }

    /**
     * Extras that can be passed to FragmentNavigator to enable Fragment specific behavior
     */
    class Extras internal constructor(sharedElements: Map<View, String>) :
        Navigator.Extras {
        private val _sharedElements = LinkedHashMap<View, String>()

        /**
         * The map of shared elements associated with these Extras. The returned map
         * is an [unmodifiable][Map] copy of the underlying map and should be treated as immutable.
         */
        val sharedElements: Map<View, String>
            get() = _sharedElements.toMap()

        /**
         * Builder for constructing new [Extras] instances. The resulting instances are
         * immutable.
         */
        class Builder {
            private val _sharedElements = LinkedHashMap<View, String>()

            /**
             * Adds multiple shared elements for mapping Views in the current Fragment to
             * transitionNames in the Fragment being navigated to.
             *
             * @param sharedElements Shared element pairs to add
             * @return this [Builder]
             */
            fun addSharedElements(sharedElements: Map<View, String>): Builder {
                for ((view, name) in sharedElements) {
                    addSharedElement(view, name)
                }
                return this
            }

            /**
             * Maps the given View in the current Fragment to the given transition name in the
             * Fragment being navigated to.
             *
             * @param sharedElement A View in the current Fragment to match with a View in the
             * Fragment being navigated to.
             * @param name The transitionName of the View in the Fragment being navigated to that
             * should be matched to the shared element.
             * @return this [Builder]
             * @see `FragmentTransaction.addSharedElement`
             */
            private fun addSharedElement(sharedElement: View, name: String): Builder {
                _sharedElements[sharedElement] = name
                return this
            }

            /**
             * Constructs the final [Extras] instance.
             *
             * @return An immutable [Extras] instance.
             */
            fun build(): Extras {
                return Extras(_sharedElements)
            }
        }

        init {
            _sharedElements.putAll(sharedElements)
        }
    }

    private companion object {
        private const val TAG = "FragmentNavigator"
        private const val KEY_SAVED_IDS = "androidx-nav-fragment:navigator:savedIds"
    }
}
