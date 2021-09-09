package com.af.demo.jetpack.lifecycle

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class LifecycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this))
        lifecycle.addObserver(ActivityObserver())
    }
}
