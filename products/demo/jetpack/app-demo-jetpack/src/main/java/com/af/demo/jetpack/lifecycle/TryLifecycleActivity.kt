package com.af.demo.jetpack.lifecycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.af.demo.jetpack.R
import com.mod.lifecycle.BaseLifecycleActivity

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class TryLifecycleActivity : BaseLifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)
        findViewById<AppCompatButton>(R.id.button_show_fragment).setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_holder, TryLifecycleFragment())
            fragmentTransaction.commit()
        }
        findViewById<AppCompatButton>(R.id.button_start_service).setOnClickListener {
            val intent = Intent(this, TryLifecycleService::class.java)
            startService(intent)
        }
        findViewById<AppCompatButton>(R.id.button_stop_service).setOnClickListener {
            val intent = Intent(this, TryLifecycleService::class.java)
            stopService(intent)
        }
    }
}
