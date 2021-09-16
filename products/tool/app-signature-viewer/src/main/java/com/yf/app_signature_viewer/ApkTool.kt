package com.yf.app_signature_viewer

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import kotlin.collections.ArrayList

/**
 * APK Tool
 */
class ApkTool {

    companion object {
        fun listInstalledPackages(packageManager: PackageManager): ArrayList<AppInfo> {
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
                    packageInfo.applicationInfo.loadLabel(packageManager).toString()
                )
                list.add(appInfo)
            }
            return list
        }
    }
}
