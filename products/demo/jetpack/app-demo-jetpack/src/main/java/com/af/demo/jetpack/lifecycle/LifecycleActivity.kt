package com.af.demo.jetpack.lifecycle

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.af.demo.jetpack.R

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class LifecycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)
        findViewById<AppCompatButton>(R.id.button_show_fragment).setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_holder, LifecycleFragment())
            fragmentTransaction.commit()
        }
        lifecycle.addObserver(ActivityObserver())
    }
}
