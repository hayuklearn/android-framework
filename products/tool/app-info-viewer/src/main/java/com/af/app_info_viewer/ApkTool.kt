package com.af.app_info_viewer

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.af.lib.utils.Android
import kotlin.collections.ArrayList

/**
 * APK Tool
 */
class ApkTool {

    companion object {
        fun listInstalledPackages(context: Context, packageManager: PackageManager): ArrayList<AppInfo> {
            val list: ArrayList<AppInfo> = ArrayList()
            val packageList: List<PackageInfo> =
                packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)

            for (packageInfo in packageList) {

                val appInfo = AppInfo(
                    //
                    packageInfo.applicationInfo.loadIcon(packageManager),
                    //
                    packageInfo.packageName,
                    //
                    packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                    //
                    Android.signature(context, packageInfo.packageName)
                )
                list.add(appInfo)
            }
            return list
        }
    }
}
