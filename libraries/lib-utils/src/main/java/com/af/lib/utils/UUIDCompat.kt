package com.af.lib.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.af.lib.compat.AndroidVersionCompat
import com.af.lib.compat.AndroidVersionCompatible
import com.af.lib.ktext.toast
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
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

    suspend fun getUUID(activity: AppCompatActivity): String? = withContext(Dispatchers.Main) {

        val sn = SNCompat.getSN()

        val imei = IMEICompat.getIMEI(activity)

        return@withContext suspendCoroutine { continuation ->

            AndroidVersionCompat.compat(object : AndroidVersionCompatible {

                override fun compatWithQ(): Boolean {

                    val directory = "${Environment.getExternalStorageDirectory()}/lyy/${activity.packageName}"
                    val directoryFile = File(directory)
                    if (!directoryFile.exists()) {
                        directoryFile.mkdirs()
                    }
                    val path = "$directory/uuid"
                    val file = File(path)
                    if (!file.isFile) {
                        // 需要获取权限
                        PermissionX.init(activity)
                            .permissions(arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            .explainReasonBeforeRequest()
                            .onExplainRequestReason { scope, deniedList ->
                                scope.showRequestReasonDialog(
                                    deniedList,
                                    "应用需要获得设备外部存储空间读写权限，用于获取设备 UUID",
                                    "同意",
                                    "拒绝"
                                )
                            }
                            .onForwardToSettings { scope, deniedList ->
                                if (deniedList.isNotEmpty()) {
                                    scope.showForwardToSettingsDialog(
                                        deniedList,
                                        "应用需要获得设备外部存储空间读写权限，用于获取设备 UUID",
                                        "去设置",
                                        "再想想"
                                    )
                                }
                            }
                            .request { allGranted, grantedList, deniedList ->

                                if (allGranted) {
                                    file.createNewFile()
                                    Log.d(TAG, "uuid save in: $path")
                                    var uuid = file.readText()
                                    if (uuid.isNotEmpty()) {
                                        continuation.resume(uuid)
                                    } else {
                                        val deviceId = SystemProperties.getRoBuildFingerprint()
                                        uuid = UUIDBean(sn, imei, deviceId).toUUID()
                                        file.writeText(uuid)
                                        continuation.resume(uuid)
                                    }
                                }
                            }
                    } else {
                        Log.d(TAG, "uuid save in: $path")
                        var uuid = file.readText()
                        if (uuid.isNotEmpty()) {
                            continuation.resume(uuid)
                        } else {
                            val deviceId = SystemProperties.getRoBuildFingerprint()
                            uuid = UUIDBean(sn, imei, deviceId).toUUID()
                            file.writeText(uuid)
                            continuation.resume(uuid)
                        }
                    }
                    return true
                }

                override fun compatWithDefault() {

                    val deviceId = SystemProperties.getRoBuildFingerprint()
                    val uuid = UUIDBean(sn, imei, deviceId).toUUID()
                    continuation.resume(uuid)

                    // 根据设备信息生成的 UUID
                    // val t = ("35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10)
                    // val deviceId: String = UUID(t.hashCode().toLong(), sn.hashCode().toLong()).toString()
                    // val uuid = UUIDBean(sn, imei, deviceId).toUUID()
                    // continuation.resume(uuid)
                }
            })
        }
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


