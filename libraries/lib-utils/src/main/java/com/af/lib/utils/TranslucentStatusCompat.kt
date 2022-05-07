package com.af.lib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat

/**
 * Translucent Status
 *
 * @author liangxiaxu@leyaoyao.com
 */
object TranslucentStatusCompat {

    @SuppressLint("ObsoleteSdkInt")
    private val IMPL: TranslucentStatusCompatImpl = when {

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            RTranslucentStatusCompatImpl()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            MTranslucentStatusCompatImpl()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            LollipopTranslucentStatusCompatImpl()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            KitKatTranslucentStatusCompatImpl()
        }
        else -> {
            DefaultTranslucentStatusCompatImpl()
        }
    }

    fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        this.IMPL.requestTranslucentStatus(activity, darkStatusBarIconAndText)
    }
}

private interface TranslucentStatusCompatImpl {

    /**
     *
     * @param activity Activity
     * @param darkStatusBarIconAndText Boolean 是否深色图标与文字
     */
    fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean)
}

@TargetApi(Build.VERSION_CODES.R)
private class RTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        val window = activity.window
        window.setDecorFitsSystemWindows(false)
        window.statusBarColor = Color.TRANSPARENT

        val windowInsetsController = ViewCompat.getWindowInsetsController(activity.window.decorView)
        windowInsetsController?.isAppearanceLightStatusBars = darkStatusBarIconAndText
    }
}

@TargetApi(Build.VERSION_CODES.M)
private class MTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        val window = activity.window
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

        if (darkStatusBarIconAndText) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private class LollipopTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        val window = activity.window
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (darkStatusBarIconAndText) {
            window.statusBarColor = Color.parseColor("#88000000")
        } else {
            window.statusBarColor = Color.parseColor("#22000000")
        }
    }
}

@TargetApi(Build.VERSION_CODES.KITKAT)
private class KitKatTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {

        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
    }
}

private class DefaultTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity, darkStatusBarIconAndText: Boolean) {
        // do nothing
    }
}