package com.af.info.app

import android.graphics.drawable.Drawable

/**
 * 应用信息
 *
 * Created by hayukleung@gmail.com on 2021-09-16.
 *
 * @param image 应用图标
 * @param packageName 应用包名
 * @param appName 应用名称
 * @param signature 应用签名
 */
data class AppInfo(
     var image: Drawable,
     var packageName: String,
     var appName: String,
     var signature: String
)
