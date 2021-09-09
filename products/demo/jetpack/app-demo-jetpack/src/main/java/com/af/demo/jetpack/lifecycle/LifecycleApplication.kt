package com.af.demo.jetpack.lifecycle

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class LifecycleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // ProcessLifecycleOwner.get().lifecycle.addObserver()
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationObserver())
    }
}
