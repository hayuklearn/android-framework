package com.af.app_view.moon_view

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.af.app_view.R

/**
 *
 *
 * @author hayuk
 * @date 2021-09-23
 */
class MoonViewActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    var moonView: MoonView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moon_view)
        moonView = findViewById<MoonView>(R.id.MoonView)

        timer = object : CountDownTimer(30_000, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                moonView?.mPhase = (millisUntilFinished / 100L).toInt()
            }

            override fun onFinish() {
            }
        }
        timer?.start()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d("MainActivity", "onConfigurationChanged#${newConfig.orientation}")
        super.onConfigurationChanged(newConfig)
        moonView?.mRotate =
            if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                90
            } else {
                0
            }
    }
}
