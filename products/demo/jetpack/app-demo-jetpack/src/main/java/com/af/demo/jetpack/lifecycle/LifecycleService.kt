package com.af.demo.jetpack.lifecycle

import androidx.lifecycle.LifecycleService
import com.af.demo.jetpack.lifecycle.observer.ServiceObserver

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class LifecycleService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(ServiceObserver())
    }
}
