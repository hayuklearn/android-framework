package com.af.lib.utils

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible

/**
 * Translucent Status
 *
 * @author liangxiaxu@leyaoyao.com
 */
object TranslucentStatusCompat {

    /**
     *
     * @param activity Activity
     * @param darkStatusBarIconAndText Boolean 是否深色图标与文字
     */
    fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        AndroidVersionCompat.compat(object : AndroidVersionCompatible {

            @TargetApi(Build.VERSION_CODES.R)
            override fun compatWithR(): Boolean {

                val window = activity.window
                window.setDecorFitsSystemWindows(false)
                window.statusBarColor = Color.TRANSPARENT
                val windowInsetsController = ViewCompat.getWindowInsetsController(activity.window.decorView)
                windowInsetsController?.isAppearanceLightStatusBars = darkStatusBarIconAndText
                return true
            }

            override fun compatWithM(): Boolean {

                val window = activity.window
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                window.statusBarColor = Color.TRANSPARENT
                if (darkStatusBarIconAndText) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                return true
            }

            override fun compatWithLollipop(): Boolean {

                val window = activity.window
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                if (darkStatusBarIconAndText) {
                    window.statusBarColor = Color.parseColor("#88000000")
                } else {
                    window.statusBarColor = Color.parseColor("#22000000")
                }
                return true
            }

            override fun compatWithKitkat(): Boolean {

                activity.window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                return true
            }
        })
    }
}
