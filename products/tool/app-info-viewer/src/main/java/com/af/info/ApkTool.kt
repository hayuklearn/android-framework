package com.af.info

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.af.info.app.AppInfo
import com.af.info.service.ServiceInfo
import com.af.lib.utils.Android
import kotlin.collections.ArrayList
import android.content.Context.ACTIVITY_SERVICE

import androidx.core.content.ContextCompat.getSystemService

import android.app.ActivityManager
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception


/**
 * APK Tool
 */
object ApkTool {


    fun listInstalledPackages(
        context: Context,
        packageManager: PackageManager
    ): ArrayList<AppInfo> {
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


    fun shellExec() {
        val runtime = Runtime.getRuntime()
        try {
            // Process 中封装了返回的结果和执行错误的结果
            // val process = runtime.exec("su")
            val process =
                runtime.exec("su dumpsys activity services | grep ServiceRecord | awk '{print \$4}' | sed 's/}//1g'")
                // runtime.exec("su")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val respBuff = StringBuffer()
            val buff = CharArray(1024)
            var ch: Int = 0
            while (reader.read(buff).also { ch = it } != -1) {
                respBuff.append(buff, 0, ch)
            }
            reader.close()
            Log.d("[shell]", respBuff.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun exec() {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = "dumpsys activity services | grep ServiceRecord | awk '{print \$4}' | sed 's/}//1g'"
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val respBuff = StringBuffer()
            val buff = CharArray(65536)
            var ch: Int = 0
            while (reader.read(buff).also { ch = it } != -1) {
                respBuff.append(buff, 0, ch)
            }
            reader.close()
            Log.d("[shell]", respBuff.toString())
            process.waitFor()
        } catch (e: Exception) {
        } finally {
            os?.close()
            process?.destroy()
        }
    }

}
