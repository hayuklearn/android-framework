package com.af.lib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * Translucent Status
 *
 * @author liangxiaxu@leyaoyao.com
 */
object TranslucentStatusCompat {

    @SuppressLint("ObsoleteSdkInt")
    private val IMPL: TranslucentStatusCompatImpl = when {

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            LollipopTranslucentStatusCompatImpl()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            KitKatTranslucentStatusCompatImpl()
        }
        else -> {
            BaseViewCompatImpl()
        }
    }

    fun requestTranslucentStatus(activity: Activity) {

        this.IMPL.requestTranslucentStatus(activity)
    }
}

private interface TranslucentStatusCompatImpl {

    fun requestTranslucentStatus(activity: Activity)
}

private class BaseViewCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity) {
        // do nothing
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private class LollipopTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {
    override fun requestTranslucentStatus(activity: Activity) {
        val window = activity.window
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }
}

@TargetApi(Build.VERSION_CODES.KITKAT)
private class KitKatTranslucentStatusCompatImpl : TranslucentStatusCompatImpl {

    override fun requestTranslucentStatus(activity: Activity) {

        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
    }
}