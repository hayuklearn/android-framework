package com.af.info

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.af.info.app.AppInfo
import com.af.lib.ktext.signature
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**
 * APK Tool
 */
object ApkTool {

    suspend fun listInstalledPackages(
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
                context.signature(packageInfo.packageName)
            )
            list.add(appInfo)
        }
        return list
    }

    /**
     * 执行终端命令
     *
     * @param cmd String val cmd = "dumpsys activity services | grep ServiceRecord | awk '{print \$4}' | sed 's/}//1g'"
     * @return String 运行结果
     */
    fun exec(cmd: String): String {
        var result: String
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val respBuff = StringBuffer()
            val buff = CharArray(65536)
            var ch: Int
            while (reader.read(buff).also { ch = it } != -1) {
                respBuff.append(buff, 0, ch)
            }
            reader.close()
            result = respBuff.toString()
            Log.d("[shell]", respBuff.toString())
            process.waitFor()
        } catch (e: Exception) {
            result = Log.getStackTraceString(e)
        } finally {
            os?.close()
            process?.destroy()
        }
        return result
    }
}
