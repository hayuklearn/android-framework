package com.af.lib.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * 目的是让分布式系统中的所有元素，都能有唯一的辨识信息，
 * 而不需要通过中央控制端来做辨识信息的指定。
 * UUID 是指在一台机器上生成的数字，由当前日期和时间、时钟序列、全局唯一的 IEEE 机器识别号等几部分组合形成，
 * 它可以保证对在同一时空中的所有机器都是唯一的
 * UUID 在一般情况下很难生成一致的编码
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-05-25
 */
object UUIDCompat {

    private const val TAG = "uuid"

    suspend fun getUUID(activity: AppCompatActivity, androidRAllFilesAccessPermissionRequestCode: Int): String? = withContext(Dispatchers.Main) {

        val newUUID = newUUID(activity)

        return@withContext suspendCoroutine { continuation ->

            AndroidVersionCompat.compat(object : AndroidVersionCompatible {

                @RequiresApi(Build.VERSION_CODES.R)
                override fun compatWithR(): Boolean {
                    // 需要获取权限
                    if (Environment.isExternalStorageManager()) {
                        val uuidFile = getUUIDFile(activity.packageName)
                        if (!uuidFile.isFile) {
                            uuidFile.deleteRecursively()
                            uuidFile.createNewFile()
                        }
                        val uuid = uuidFile.readText()
                        if (uuid.isNotEmpty()) {
                            continuation.resume(uuid)
                        } else {
                            uuidFile.writeText(newUUID)
                            continuation.resume(newUUID)
                        }
                    } else {
                        // 请求用户授权
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:${activity.packageName}")
                        activity.startActivityForResult(intent, androidRAllFilesAccessPermissionRequestCode)
                    }
                    return true
                }

                override fun compatWithO(): Boolean {
                    // Android 8.0 及以上系统采用新版获取 UUID 方式
                    // 需要获取权限
                    val reason = "应用需要获得设备外部存储空间读写权限，用于获取设备 UUID"
                    PermissionX.init(activity)
                        .permissions(arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .explainReasonBeforeRequest()
                        .onExplainRequestReason { scope, deniedList ->
                            scope.showRequestReasonDialog(
                                deniedList,
                                reason,
                                "同意",
                                "拒绝"
                            )
                        }
                        .onForwardToSettings { scope, deniedList ->
                            if (deniedList.isNotEmpty()) {
                                scope.showForwardToSettingsDialog(
                                    deniedList,
                                    reason,
                                    "去设置",
                                    "再想想"
                                )
                            }
                        }
                        .request { allGranted, _, _ ->
                            if (allGranted) {
                                val uuidFile = getUUIDFile(activity.packageName)
                                if (!uuidFile.isFile) {
                                    uuidFile.deleteRecursively()
                                    uuidFile.createNewFile()
                                }
                                val uuid = uuidFile.readText()
                                if (uuid.isNotEmpty()) {
                                    continuation.resume(uuid)
                                } else {
                                    uuidFile.writeText(newUUID)
                                    continuation.resume(newUUID)
                                }
                            }
                        }
                    return true
                }

                override fun compatWithDefault() {
                    // 兼容传统售货机、兑币机设备
                    // Android 7.1 及以下系统
                    val uuid = UUIDLegacy.getDeviceUniqueId(activity, UUIDLegacy.getSerial())
                    continuation.resume(uuid)
                }
            })
        }
    }

    private fun getUUIDFile(packageName: String): File {

        val directory = "${Environment.getExternalStorageDirectory()}/lyy/${packageName}"
        val directoryFile = File(directory)
        if (!directoryFile.exists()) {
            directoryFile.mkdirs()
        }
        val uuidPath = "$directory/uuid"
        val uuidFile = File(uuidPath)
        Log.d(TAG, "uuid file: ${uuidFile.absolutePath}")
        return uuidFile
    }

    private suspend fun newUUID(context: Context): String {

        val sn = SNCompat.getSN()
        val imei = IMEICompat.getIMEI(context)
        val deviceId = SystemProperties.getRoBuildFingerprint()
        return UUIDBean(sn, imei, deviceId).toUUID()
    }
}

data class UUIDBean(val sn: String, val imei: String, val deviceId: String) {

    fun toUUID() = MD5.md5(toString())

    override fun toString(): String {

        val jo = JSONObject()
        jo.put("os", Build.VERSION.SDK_INT)
        jo.put("sn", sn)
        jo.put("imei", imei)
        jo.put("deviceId", deviceId)
        return jo.toString()
    }
}


