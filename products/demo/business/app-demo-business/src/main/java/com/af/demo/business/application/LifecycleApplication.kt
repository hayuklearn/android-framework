package com.af.demo.business.application

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class LifecycleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationObserver())
    }
}
